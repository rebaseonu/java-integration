package se.sveaekonomi.webpay.integration.hosted.payment;

import javax.xml.stream.XMLStreamWriter;

import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;

public class FakeHostedPayment extends HostedPayment<FakeHostedPayment> {

    public FakeHostedPayment(CreateOrderBuilder orderBuilder) {
        super(orderBuilder);
    }
    
    protected FakeHostedPayment configureExcludedPaymentMethods() {
        return this;
    }
    
    public XMLStreamWriter getPaymentSpecificXml(XMLStreamWriter xmlw) {
        return xmlw;
    }
}
