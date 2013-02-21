package se.sveaekonomi.webpay.integration.hosted.helper;

import java.util.HashMap;
import java.util.Map;

import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.security.HashUtil;
import se.sveaekonomi.webpay.integration.util.security.HashUtil.HASHALGORITHM;

public class PaymentForm {

    private String messageBase64;
    private String message;
    private String merchantid;
    private String secretWord;
    private String testmode;
    private String form;
    private String macSha512;
    private String url;
    private Map<String, String> formHtmlFields;
    private String submitText;
    private String noScriptMessage;

    public PaymentForm() {
        formHtmlFields = new HashMap<String, String>();
        setSubmitMessage(COUNTRYCODE.NL);
    }
    
    public void setSubmitMessage(COUNTRYCODE countryCode) {
        switch(countryCode) {
            case SE:
                this.setSubmitText("Betala");
                this.setNoScriptMessage("Javascript �r inaktiverat i er webbl�sare, s� ni f�r manuellt omdirigera till paypage");
                break;
            default:
                this.setSubmitText("Submit");
                this.setNoScriptMessage("Javascript is inactivated in your browser, you will manually have to redirect to the paypage");            
        }
    }
    
    public String getMessageBase64() {
        return messageBase64;
    }
    
    public PaymentForm setMessageBase64(String messageBase64) {
        this.messageBase64 = messageBase64;
        return this;
    }
    
    public String getMerchantId() {
        return merchantid;
    }
    
    public PaymentForm setMerchantId(String merchantid) {
        this.merchantid = merchantid;
        return this;
    }
    
    public String getSecretWord() {
        return secretWord;
    }
    
    public PaymentForm setSecretWord(String secretWord) {
        this.secretWord = secretWord;
        return this;
    }
    
    public String getTestmode() {
        return testmode;
    }
    
    public PaymentForm setTestmode(String testmode) {
        this.testmode = testmode;
        return this;
    }
    
    public String getForm() {
        return form;
    }
    
    public PaymentForm setMacSha512(String macSha512) {
        this.macSha512 = macSha512;
        return this;
    }
    
    public String getMacSha512() {
        return this.macSha512;
    }
    
    public String getUrl() {
        return url;
    }
    
    public Map<String, String> getFormHtmlFields() {
        return formHtmlFields;
    }

    public PaymentForm setForm() {
        url = (testmode != null ? SveaConfig.SWP_TEST_URL : SveaConfig.SWP_PROD_URL);
        macSha512 = HashUtil.createHash(messageBase64 + secretWord, HASHALGORITHM.SHA_512);
        
        form = "<form name=\"paymentForm\" id=\"paymentForm\" method=\"post\" action=\""
                + url
                + "\">"
                + "<input type=\"hidden\" name=\"merchantid\" value=\"" + merchantid + "\" />"
                + "<input type=\"hidden\" name=\"message\" value=\"" + messageBase64 + "\" />"
                + "<input type=\"hidden\" name=\"mac\" value=\"" + macSha512 + "\" />"
                + "<noscript><p>" + this.noScriptMessage + "</p></noscript>"
                + "<input type=\"submit\" name=\"submit\" value=\"" + this.submitText + "\" />"
                + "</form>";
        
        return this;
    }
    
    public PaymentForm setHtmlFields() {
        url = (testmode != null ? SveaConfig.SWP_TEST_URL : SveaConfig.SWP_PROD_URL);
        macSha512 = HashUtil.createHash(messageBase64 + secretWord, HASHALGORITHM.SHA_512);
        
        formHtmlFields.put("form_start_tag", "<form name=\"paymentForm\" id=\"paymentForm\" method=\"post\" action=\"" + url + "\">");
        formHtmlFields.put("input_merchantId", "<input type=\"hidden\" name=\"merchantid\" value=\"" + merchantid + "\" />");
        formHtmlFields.put("input_message", "<input type=\"hidden\" name=\"message\" value=\"" + messageBase64 + "\" />");
        formHtmlFields.put("input_mac", "<input type=\"hidden\" name=\"mac\" value=\"" + macSha512 + "\" />");
        formHtmlFields.put("noscript_p_tag", "<noscript><p>" + this.noScriptMessage + "</p></noscript>");
        formHtmlFields.put("input_submit", "<input type=\"submit\" name=\"submit\" value=\""+ this.submitText +"\" />");
        formHtmlFields.put("form_end_tag", "</form>");
        
        return this;
    }

    public String getSubmitText() {
        return submitText;
    }

    public void setSubmitText(String submitText) {
        this.submitText = submitText;
    }

    public String getNoScriptMessage() {
        return noScriptMessage;
    }

    public void setNoScriptMessage(String noScriptMessage) {
        this.noScriptMessage = noScriptMessage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}