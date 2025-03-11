package com.ecommerce.payment._service.config.VNPayConfig;

import com.ecommerce.payment._service.dto.request.PaymentRequest;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@Configuration
public class VNPayConfig {
    @Getter
    @Value("${payment.vnPay.url}")
    private String vnp_PayUrl;

    @Value("${payment.vnPay.returnUrl}")
    private String vnp_ReturnUrl;

    @Value("${payment.vnPay.tmnCode}")
    private String vnp_TmnCode;

    @Getter
    @Value("${payment.vnPay.secretKey}")
    private String vnp_SecretKey;

    @Value("${payment.vnPay.version}")
    private String vnp_Version;
    @Value("${payment.vnPay.command}")
    private String vnp_Command;

    public Map<String, String> getVNPAYConfig(PaymentRequest paymentRequest) {
        Map<String, String> vnp_Params = new HashMap<>();

        vnp_Params.put("vnp_Version", this.vnp_Version);
        vnp_Params.put("vnp_Command", this.vnp_Command);
        vnp_Params.put("vnp_TmnCode", this.vnp_TmnCode);
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef",  paymentRequest.getTransactionId());
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" +  paymentRequest.getTransactionId());
        vnp_Params.put("vnp_OrderType", paymentRequest.getOrderType());
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", this.vnp_ReturnUrl);

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnpCreateDate = sdf.format(cal.getTime());
        vnp_Params.put("vnp_CreateDate", vnpCreateDate);
        cal.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = sdf.format(cal.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        return vnp_Params;
    }
}
