package org.openjfx.token.models;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.*;
import com.itextpdf.io.image.ImageDataFactory;
import org.openjfx.Main.FXMLController;
import org.openjfx.Main.Start;
import org.openjfx.Main.file.exceptions.BadPasswordTokenException;
import org.openjfx.infrastructure.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

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
        this.signWithPositionStamper(src,dst,40,40); // Dejo como estaba todo antes
    }

    public void signWithPositionStamper(String src, String dst, int posX,int posY) throws GeneralSecurityException, BadPasswordTokenException {
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
                    "-", "Viedma, Río Negro, Argentina", posX, posY);

            // TODO: 6/10/20 excepcion por null

        } catch (IOException e) {
            throw new BadPasswordTokenException();
        }

    }

    public void processSign(String src, String dest,
                            Certificate[] chain, PrivateKey pk, String digestAlgorithm, String provider,
                            //MakeSignature.CryptoStandard subfilter,
                            PdfSigner.CryptoStandard subfilter,
                            String reason, String location, int posX, int posY)
            throws GeneralSecurityException, IOException {

        // Creating the reader and the stamper
        PdfReader reader = new PdfReader(src);
        reader.setUnethicalReading(true);
        //Rectangle lala = reader.getPageSize(reader.getNumberOfPages());
        FileOutputStream os = new FileOutputStream(dest);

        PdfSigner signer = new PdfSigner(reader, new FileOutputStream(dest), new StampingProperties());

        Rectangle rect = new Rectangle(posX, posY, posX+320, posY + 40);
        PdfSignatureAppearance appearance = signer.getSignatureAppearance();
        appearance.setLayer2FontSize(5f);
        appearance.setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC_AND_DESCRIPTION);
        appearance.setSignatureGraphic(ImageDataFactory.create(FXMLController.class.getResource("telegram.png")));
        appearance.setImageScale(-1);
        appearance.setReasonCaption("QWEQWEQWE");
        // appearance.setLayer2Text("Replace default text");

        //appearance.setLayer2Font(PdfFontFactory.createFont("Arial", "ISO-8859-1", true));


        //PdfStamper stamper = PdfStamper.createSignature(reader, os, '\0',null,true);
        // Creating the appearance
        //PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
        appearance.setReason(reason);
        appearance.setLocation(location);
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

        appearance.setPageRect(rect)
                .setPageNumber(signer.getDocument().getNumberOfPages())
                .setReason(reason)
                .setLocation(location);

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
        reader.close();
        os.close();
        //OCSPVerifier ocspVerifier = new OCSPVerifier(null, null);
        //IOcspClient ocsp = new OcspClientBouncyCastle(null);
        //addLTV(dest, dest + "ltv.pdf", ocsp, new CrlClientOnline(), LtvVerification.Level.OCSP_CRL, LtvVerification.Level.OCSP_CRL);
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
