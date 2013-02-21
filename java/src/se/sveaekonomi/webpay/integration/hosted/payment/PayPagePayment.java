package se.sveaekonomi.webpay.integration.hosted.payment;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamWriter;

import se.sveaekonomi.webpay.integration.hosted.helper.ExcludePayments;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTMETHOD;

public class PayPagePayment extends HostedPayment {

	protected PAYMENTMETHOD paymentMethod;
	protected List<PAYMENTMETHOD> includedPaymentMethods;

	public PayPagePayment(CreateOrderBuilder orderBuilder) {
		super(orderBuilder);
		includedPaymentMethods = new ArrayList<PAYMENTMETHOD>();
	}

	public PAYMENTMETHOD getPaymentMethod() {
		return paymentMethod;
	}

	public PayPagePayment setPaymentMethod(PAYMENTMETHOD paymentMethod) {
		this.paymentMethod = paymentMethod;
		return this;
	}

	public List<PAYMENTMETHOD> getIncludedPaymentMethods() {
		return includedPaymentMethods;
	}

	public HostedPayment setIncludedPaymentMethods(
			List<PAYMENTMETHOD> paymentMethods) {
		this.includedPaymentMethods.addAll(paymentMethods);
		includePaymentMethods();
		return this;
	}

	/**
	 * Only used in CardPayment and DirectPayment
	 */
	protected HostedPayment configureExcludedPaymentMethods() {
		return this;
	}

	public PayPagePayment excludeCardPaymentMethods() {
		excludedPaymentMethods.add(PAYMENTMETHOD.KORTCERT);
		excludedPaymentMethods.add(PAYMENTMETHOD.SKRILL);
		return this;
	}

	public PayPagePayment excludeDirectPaymentMethods() {
		excludedPaymentMethods.add(PAYMENTMETHOD.DBNORDEASE);
		excludedPaymentMethods.add(PAYMENTMETHOD.DBSEBSE);
		excludedPaymentMethods.add(PAYMENTMETHOD.DBSEBFTGSE);
		excludedPaymentMethods.add(PAYMENTMETHOD.DBSHBSE);
		excludedPaymentMethods.add(PAYMENTMETHOD.DBSWEDBANKSE);
		return this;
	}

	protected PayPagePayment includePaymentMethods() {
		// Exclude all payment methods
		ExcludePayments excluded = new ExcludePayments();
		excludedPaymentMethods = excluded.excludeInvoicesAndPaymentPlan();

		excludedPaymentMethods.add(PAYMENTMETHOD.KORTCERT);
		excludedPaymentMethods.add(PAYMENTMETHOD.SKRILL);
		excludedPaymentMethods.add(PAYMENTMETHOD.PAYPAL);

		// Remove the included methods from the excluded payment methods
		for (PAYMENTMETHOD pm : includedPaymentMethods) {
			excludedPaymentMethods.remove(pm);
		}

		return this;
	}

	public PayPagePayment setPayPageLanguage(String languageCodeAsISO639) {
		if ("sv".equals(languageCodeAsISO639)
				|| "fi".equals(languageCodeAsISO639)
				|| "es".equals(languageCodeAsISO639)
				|| "en".equals(languageCodeAsISO639)) {
			this.languageCode = languageCodeAsISO639;
		} else {
			this.languageCode = "en";
		}
		return this;
	}

	public XMLStreamWriter getPaymentSpecificXml(XMLStreamWriter xmlw)
			throws Exception {
		if (paymentMethod != null) {
			writeSimpleElement(xmlw, "paymentmethod", paymentMethod.toString());
		}

		return xmlw;
	}
}