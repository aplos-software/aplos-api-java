
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Main {
    private static final String APLOS_DOMAIN = "www.aplos.com";
    private static final String APLOS_API = "api.key";
    private static final String APLOS_PRIVATE = "private.key";

    public static void main(String... args) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException,
            InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        String key = Main.resolveKey();
        String url = "/hermes/api/v1/auth/" + key;
        URL aplosUrl = new URL("https", APLOS_DOMAIN, url);
        URLConnection aplosConn = aplosUrl.openConnection();
        String authResult = getUrlResults(aplosConn);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Token t = gson.fromJson(authResult, Token.class);
        String token = t.getData().getToken();
        String decryptToken = decryptToken(token);

        String urlAccounts = "/hermes/api/v1/accounts";
        URL accountsURL = new URL("https", APLOS_DOMAIN, urlAccounts);
        URLConnection accountsConn = accountsURL.openConnection();
        accountsConn.setRequestProperty("Authorization", "Bearer: " + decryptToken);
        String urlResults = getUrlResults(accountsConn);
        System.out.println(urlResults);
    }

    private static String getUrlResults(URLConnection urlc) {
        StringBuilder sbResults = new StringBuilder();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
            String results;
            while ((results = reader.readLine()) != null) {
                sbResults.append(results);
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
        return sbResults.toString();
    }

    private static String decryptToken(String token)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] bytes = Files.readAllBytes(Paths.get(APLOS_PRIVATE));
        byte[] decode = Base64.getDecoder().decode(bytes);
        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(decode);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey pvt = kf.generatePrivate(ks);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, pvt);
        byte[] decryptedToken = cipher.doFinal(Base64.getDecoder().decode(token));
        // String decryptedToken = new String(bytes, "UTF-8");
        return new String(decryptedToken, "UTF-8");
    }

    private static final String resolveKey() throws IOException {
        String apiKey = new String(Files.readAllBytes(Paths.get(APLOS_API)));
        return apiKey;
    }
}
