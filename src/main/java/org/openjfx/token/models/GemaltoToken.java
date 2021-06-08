package org.openjfx.token.models;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.DashedBorder;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.signatures.*;
import com.itextpdf.io.image.ImageDataFactory;
import org.openjfx.Main.FXMLController;
import org.openjfx.Main.Start;
import org.openjfx.Main.file.exceptions.BadPasswordTokenException;
import org.openjfx.infrastructure.Log;

import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class GemaltoToken implements Token {

    private static final long TICKS_POR_DIA = 1000 * 60 * 60 * 24;
    private final static Log LOGGER = new Log();
    //protected ExternalSignature signature =null;
    protected IExternalSignature signature = null;
    protected String driverPath;
    private char[] pwd;
    protected Provider provider;

    public GemaltoToken(String pwd)
    {
        this.driverPath = "";
        Provider prototype = Security.getProvider("SunPKCS11");
        this.provider = this.configureProvider(prototype);

        /*this.provider = prototype.configure("--name=eToken\n" +
                "library=/lib64/libeToken.so\n" +
                "slot=0");*/
        this.pwd = pwd.toCharArray();
        Security.addProvider(provider);
    }

    private Provider configureProvider(Provider prototype) {
        ConfigureProvider providerBundle = new ConfigureProvider();
        String type = "";
        if (System.getProperty("os.name").toLowerCase().contains("linux") ||
                System.getProperty("os.name").toLowerCase().contains("sunos") ||
                System.getProperty("os.name").toLowerCase().contains("solaris")
        ) {
            type = "linux";
        } else if (
                System.getProperty("os.name").toLowerCase().contains("mac os x")
        ) {
            type = "mac";
        } else {
            type = "windows";
        }

        ArrayList<String> configs = new ArrayList<String>();
        ArrayList<LocalProvider> providers = providerBundle.getProviders(type);
        for (int n = 0; n < providers.size(); n++) {
            try {
                File libraryFile = new File(providers.get(n).getLibrary());
                //System.out.println("Path al archivo: " + libraryFile.getPath());
                if (libraryFile.exists()) {
                    //configs.add("--name=" + providers.get(n).getName() + "\nlibrary=" + libraryFile.getPath());
                    return prototype.configure("--name=" + providers.get(n).getName() + "\nlibrary=" + libraryFile.getPath());
                }

            } catch (Exception e) {
                //e.printStackTrace();
                //System.out.println("cargarConfiguracionProviderToken error: " + e.getMessage());
                LOGGER.warning("ERROR al obtener el driver del token. Posiblemente no se encuentre el archivo. :::response:" + e.getMessage());

                //cargarMensajeDeError("", "cargarConfiguracionProviderToken", e);
            }
        }
        return null;
    }

    private KeyStore getKeystoreInstance() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        //try {
        KeyStore ks = KeyStore.getInstance("PKCS11");
        ks.load(null,pwd);

        return ks;
        /*} catch (KeyStoreException e) {
        } catch (IOException e) {
            Security.removeProvider(provider.getName());
        } catch (Exception e) {
            System.out.println("Other exception");
        }*/
    }

    private X509Certificate getCert() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, BadPasswordTokenException {
        X509Certificate cert = null;
        try {
            KeyStore ks = getKeystoreInstance();
            if(ks == null)
                throw new NullPointerException();
            Enumeration<String> aliases = ks.aliases();

            if(aliases != null){
                String alias = aliases.nextElement();
                cert = (X509Certificate) ks.getCertificate(alias);
            }

            if (cert == null) {
                LOGGER.warning("ERROR al obtener el driver del token. Posiblemente no se encuentre el archivo.");
                throw new CertificateException("Certificado no encontrado.");
            }

            return cert;
        } catch (IOException e) {
            throw new BadPasswordTokenException();
        }
    }

    @Override
    public Map<String,String> getInfo() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, BadPasswordTokenException {
        Map<String,String> map = new HashMap<>();
        X509Certificate cert = getCert();
        map.put("issuer",cert.getIssuerDN().toString());
        map.put("not_before",cert.getNotBefore().toString());
        map.put("not_after",cert.getNotAfter().toString());
        map.put("serial",cert.getSerialNumber().toString());
        map.put("version",Integer.toString( cert.getVersion() ));
        map.put("expire",Long.toString(expireCert(cert)));
        return map;
    }

    @Override
    public String getDriverPath() {
        return null;
    }

    @Override
    public Provider getProvider() {
        return this.provider;
    }

    protected String getConfig(){
        return Start.class.getResource("org/openjfx/pkcs11.cfg").getFile();
    }

    private long expireCert(X509Certificate cert)
    {
        long now  = new Date().getTime();
        long to   = cert.getNotAfter().getTime();
        return ((to - now) / TICKS_POR_DIA);
    }

    public void sign(String src, String dst) throws GeneralSecurityException, BadPasswordTokenException {
        this.signWithPositionStamper(src,dst,40,40, null, null, null, "Viedma, Río Negro, Argentina", null); // Dejo como estaba todo antes
    }

    public void signWithPositionStamper(String src, String dst, int posX,int posY) throws GeneralSecurityException, BadPasswordTokenException {
        signWithPositionStamper(src, dst, posX, posY, null, null, null, "Viedma, Río Negro, Argentina", null);

    }
    public void signWithPositionStamper(
            String src, String dst, int posX,int posY, String stampImage,
            String stampReason, String stampOccupation, String stampLocation, String stampName
    ) throws GeneralSecurityException, BadPasswordTokenException {
        try {
            KeyStore ks = getKeystoreInstance();
            if(ks == null){
                LOGGER.warning("ERROR, no se puede firmar porque no se encuentra la clave del token.");
                throw new NullPointerException();
            }

            Enumeration<String> aliases = ks.aliases();

            aliases = ks.aliases();
            PrivateKey privKey = null;
            Certificate[] chain = null;
            if (aliases != null){
                String alias = aliases.nextElement();
                privKey = (PrivateKey) ks.getKey(alias, pwd);
                chain = ks.getCertificateChain(alias);
            }

            if(signature == null && privKey != null) {
                String providerName = ks.getProvider().getName();
                signature =
                        new PrivateKeySignature(
                                privKey,
                                DigestAlgorithms.SHA256,
                                providerName
                        );
            }
            processSign(src, dst, chain,privKey, DigestAlgorithms.SHA256,
                    getProvider().getName(),
                    PdfSigner.CryptoStandard.CADES,
                    // MakeSignature.CryptoStandard.CMS,
                    stampReason, stampLocation, posX, posY, processImage(stampImage), stampOccupation, stampName);

            // TODO: 6/10/20 excepcion por null

        } catch (IOException e) {
            System.out.println(e.toString());

            throw new BadPasswordTokenException();
        }

    }

    private byte[] processImage(String imageBase) {
        if (imageBase == null) return null;

        return Base64.getDecoder().decode(imageBase.getBytes());

    }

    public void processSign(String src, String dest,
                            Certificate[] chain, PrivateKey pk, String digestAlgorithm, String provider,
                            //MakeSignature.CryptoStandard subfilter,
                            PdfSigner.CryptoStandard subfilter,
                            String reason, String location, int posX, int posY, byte[] stampImage, String stampOccupation, String stampName)
            throws GeneralSecurityException, IOException {

        // Creating the reader and the stamper
        PdfReader reader = new PdfReader(src);
        reader.setUnethicalReading(true);
        //Rectangle lala = reader.getPageSize(reader.getNumberOfPages());
        FileOutputStream os = new FileOutputStream(dest);

        PdfSigner signer = new PdfSigner(reader, new FileOutputStream(dest), new StampingProperties());

        PdfSignatureAppearance appearance = signer.getSignatureAppearance();

        // appearance.setLayer2Text("Replace default text");

        //appearance.setLayer2Font(PdfFontFactory.createFont("Arial", "ISO-8859-1", true));


        //PdfStamper stamper = PdfStamper.createSignature(reader, os, '\0',null,true);
        // Creating the appearance
        //PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
        if (reason == null)
            appearance.setReason(" ");
        else
            appearance.setReason(reason);
        appearance.setLocation(location);
        appearance.setPageNumber(signer.getDocument().getNumberOfPages());
        //permitir firmado
        //int certLevel = PdfSignatureAppearance.NOT_CERTIFIED;
        //appearance.setCertificationLevel(certLevel);
        //appearance.setLayer2Font(FontFactory.getFont("Arial", 5f));
        /*if (reader.getAcroFields().getSignatureNames().size() > 0) {
            certLevel = PdfSignatureAppearance.NOT_CERTIFIED;
        }*/
        signer.setCertificationLevel(PdfSigner.NOT_CERTIFIED);


        //int lastPage = reader.getNumberOfPages();
        // 40 = 1cm
        // 40 init x | 40 init y (1cmX x 1cmY)
        // 40+120 | 40 + 40
        //appearance.setVisibleSignature(new Rectangle(posX, posY, posX+120, posY+40), lastPage, "sig"+ (new Random()).nextInt(25));

        //appearance
        //        .setPageNumber(signer.getDocument().getNumberOfPages())
        //        .setReason(reason)
        //        .setLocation(location);
        Rectangle rect = new Rectangle(posX, posY, posX + 100, posY + 40);
        appearance.setPageRect(rect);
        //signer.setFieldName("sign");
        PdfFormXObject layer2 = appearance.getLayer2();
        PdfCanvas canvas = new PdfCanvas(layer2, signer.getDocument()).setFillColor(ColorConstants.LIGHT_GRAY);

        float MARGIN = 1;
        //ImageData image;
        //if (stampImage != null) {
        //    image = ImageDataFactory.create(stampImage);
        //} else {
        //    image= ImageDataFactory.create(FXMLController.class.getResource("sign_blank.png"));
        //}
        //Color greenColor = new DeviceCmyk(1.f, 0.f, 1.f, 1f);

        //Rectangle dataRect = new Rectangle(rect.getX() + MARGIN / 2, 10, rect.getWidth() / 2 - MARGIN, rect.getHeight() - 2 * MARGIN);
        //try (Canvas layoutCanvas = new Canvas(canvas, signer.getDocument(), dataRect);) {

        designStamp(
                canvas, signer, chain, rect,
                stampName, stampOccupation, location
        );


        // signature IExternalSignature pks = new PrivateKeySignature(pk, digestAlgorithm, provider);

        IExternalDigest digest = new BouncyCastleDigest();
        // Creating the signature
        //ExternalDigest digest = new BouncyCastleDigest();

        signer.signDetached(digest, signature, chain,
                null, null, null,
                0, subfilter);
        //MakeSignature.signDetached(appearance, digest, signature, chain,
              //  null, null, null, 0, subfilter);

        //stamper.close();
        signer.getDocument().close();
        reader.close();
        os.close();
        //OCSPVerifier ocspVerifier = new OCSPVerifier(null, null);
        //IOcspClient ocsp = new OcspClientBouncyCastle(null);
        //addLTV(dest, dest + "ltv.pdf", ocsp, new CrlClientOnline(), LtvVerification.Level.OCSP_CRL, LtvVerification.Level.OCSP_CRL);
    }

    private void designStamp(PdfCanvas canvas, PdfSigner signer, Certificate[] chain, Rectangle rect, String stampName, String stampOccupation, String location) throws IOException {
        Rectangle dataRect = new Rectangle(0, 0, rect.getWidth(), rect.getHeight());
        canvas.fill();
        PdfFont regular = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

        try (Canvas layoutCanvas = new Canvas(canvas, signer.getDocument(), dataRect);) {

            Table table;
            Boolean divideTable = false;

            if (stampName != null && stampName.length() > 0) {
                float[] columnWidths = {3, 1};
                table = new Table(columnWidths);
                //name
                Paragraph name = new Paragraph();
                name.add(
                        (new Text(stampName.replaceAll(",", "\n")))
                                .setFont(bold)
                                .setFontColor(ColorConstants.BLACK)
                                .setFontSize(8)
                ).setMarginBottom(0).setPaddingBottom(0);
                //.setBorder(new DashedBorder(greenColor,1,1));
                //.setWidth(dataRect.getWidth() / 3);
                table.addCell(
                        (new Cell())
                                .setBorder(Border.NO_BORDER)
                                .add(name)
                                .setPadding(0).setMargin(0)
                );
                divideTable = true;
            } else {
                float[] columnWidths = {1};
                table = new Table(columnWidths);
            }

            //signature  info
            Paragraph sign = new Paragraph()
                    //.setMultipliedLeading(0.9f)
                    .setFontColor(ColorConstants.BLACK)
                    .setHorizontalAlignment(HorizontalAlignment.RIGHT);
            sign.add(new Text( "Firmado digitalmente por \n" + CertificateInfo.getSubjectFields((X509Certificate) chain[0])
                    .getField("CN") + '\n')
                    .setFontColor(ColorConstants.BLACK)
                    .setFontSize(5)
            );
            sign.add(new Text("Fecha: " + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss z")
                    .format(signer.getSignDate().getTime()) + '\n')
                    .setFontColor(ColorConstants.BLACK)
                    .setFontSize(5)
            ).setMarginBottom(0).setPaddingBottom(0);//.setBorder(new DashedBorder(greenColor,1,1))


            table.addCell(
                    (new Cell())
                            .setPadding(0).setMargin(0)
                            .setBorder(Border.NO_BORDER).add(sign)
            );
                    //.setBorder(new DashedBorder(ColorConstants.BLACK,1,1));
            layoutCanvas.add(table.setHorizontalAlignment(HorizontalAlignment.CENTER).setMarginBottom(0).setPaddingBottom(0).setSkipLastFooter(true));

            if (stampOccupation != null && stampOccupation.length() > 0){
                Paragraph position = new Paragraph();
                position.add(
                        (new Text(stampOccupation))
                                .setFontColor(ColorConstants.BLACK)
                                .setFontSize(5)
                ).setTextAlignment(TextAlignment.CENTER)
                        .setMargin(0).setSpacingRatio(0) ;
                layoutCanvas.add(position);
            }


            if (location != null && location.length()>0) {
                Paragraph locationP = new Paragraph();
                locationP.add(
                        (new Text(location))
                                .setFontColor(ColorConstants.BLACK)
                                .setFontSize(5)
                ).setHorizontalAlignment(HorizontalAlignment.CENTER).setTextAlignment(TextAlignment.CENTER)
                .setMargin(0)
                .setSpacingRatio(0);

                //.setBorder(new DashedBorder(greenColor,1,1))

                layoutCanvas.add(locationP);
            }


            /*paragraph.add(
                    new Image(image,
                            rect.getWidth() / 2 ,
                            dataRect.getHeight() - 20,
                            22f)
            );*/


            //paragraph.add(new Text("Lugar: " + appearance.getLocation() + '\n').setFontSize(5));


            //paragraph.add(new Text( appearance.getReason() + '\n').setFontSize(5));
            //paragraph.setBorder(new DashedBorder(greenColor,1,1));


            //paragraph.add(new Text("Julian <br/> Murphy"));

            //layoutCanvas.add(paragraph);
            //layoutCanvas.setBorder(new SolidBorder(ColorConstants.BLACK,2));

        }
    }

    void addLTV(String src, String dest, IOcspClient ocsp, ICrlClient crl, LtvVerification.Level timestapLevel, LtvVerification.Level signatureLevel)
            throws IOException, GeneralSecurityException {
        PdfReader   reader = new PdfReader(src);
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdfDoc = new PdfDocument(reader, writer, new StampingProperties().useAppendMode());
        LtvVerification v = new LtvVerification(pdfDoc);
        SignatureUtil signatureUtil = new SignatureUtil(pdfDoc);
        List<String> names = signatureUtil.getSignatureNames();
        String sigName = names.get(names.size() - 1);
        PdfPKCS7 pkcs7 = signatureUtil.verifySignature(sigName);

        if (pkcs7.isTsp()) {
            v.addVerification(sigName, ocsp, crl, LtvVerification.CertificateOption.WHOLE_CHAIN,
                   timestapLevel, LtvVerification.CertificateInclusion.YES);
        } else {
            for (String name : names) {
                v.addVerification(name, ocsp, crl, LtvVerification.CertificateOption.WHOLE_CHAIN,
                        signatureLevel, LtvVerification.CertificateInclusion.YES);
            }
        }
        v.merge();

        pdfDoc.close();
    }
}
