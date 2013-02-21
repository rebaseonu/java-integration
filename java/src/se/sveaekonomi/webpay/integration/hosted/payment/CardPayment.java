package se.sveaekonomi.webpay.integration.hosted.payment;

import javax.xml.stream.XMLStreamWriter;

import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTMETHOD;

public class CardPayment extends HostedPayment {
    
    public CardPayment(CreateOrderBuilder orderBuilder) {
        super(orderBuilder);
    }
    
    protected HostedPayment configureExcludedPaymentMethods() {      
        
        COUNTRYCODE countryCode = createOrderBuilder.getCountryCode();
        
        if (countryCode.equals(COUNTRYCODE.SE)) {
            excludedPaymentMethods.add(PAYMENTMETHOD.SKRILL);
        }
        
        //Payment service providers
        excludedPaymentMethods.add(PAYMENTMETHOD.PAYPAL);
        
        //Direct bank payment methods
        excludedPaymentMethods.add(PAYMENTMETHOD.DBNORDEASE);
        excludedPaymentMethods.add(PAYMENTMETHOD.DBSEBSE);
        excludedPaymentMethods.add(PAYMENTMETHOD.DBSEBFTGSE);
        excludedPaymentMethods.add(PAYMENTMETHOD.DBSHBSE);
        excludedPaymentMethods.add(PAYMENTMETHOD.DBSWEDBANKSE);                           
        
        //Invoices and payment plan      
        excludedPaymentMethods.addAll(excluded.excludeInvoicesAndPaymentPlan());        
        
        return this;
    }
    
    public XMLStreamWriter getPaymentSpecificXml(XMLStreamWriter xmlw) {
        return xmlw;
    }
}
