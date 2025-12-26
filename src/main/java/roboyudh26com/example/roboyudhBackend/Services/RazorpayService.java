package roboyudh26com.example.roboyudhBackend.Services;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Service
public class RazorpayService {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    /* ============================
       CREATE RAZORPAY ORDER
       ============================ */
    public Order createOrder(int amount) throws RazorpayException {

        RazorpayClient client =
                new RazorpayClient(keyId, keySecret);

        JSONObject options = new JSONObject();
        options.put("amount", amount * 100); // rupees → paise
        options.put("currency", "INR");
        options.put("receipt", "roboyudh_2026");
        options.put("payment_capture", 1);

        return client.orders.create(options);
    }

    /* ============================
       VERIFY PAYMENT SIGNATURE
       ============================ */
    public boolean verifyPayment(
            String orderId,
            String paymentId,
            String razorpaySignature) {

        try {
            String payload = orderId + "|" + paymentId;
            String generatedSignature =
                    hmacSHA256(payload, keySecret);

            return generatedSignature.equals(razorpaySignature);

        } catch (Exception e) {
            return false;
        }
    }

    /* ============================
       HMAC SHA256
       ============================ */
    private String hmacSHA256(String data, String secret)
            throws Exception {

        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey =
                new SecretKeySpec(secret.getBytes(), "HmacSHA256");

        mac.init(secretKey);
        byte[] hash = mac.doFinal(data.getBytes());

        return Hex.encodeHexString(hash);
    }
}

