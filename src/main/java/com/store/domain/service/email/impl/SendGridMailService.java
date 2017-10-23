package com.store.domain.service.email.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.inject.Named;

import org.apache.commons.lang.StringUtils;

import com.google.inject.Inject;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Personalization;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.store.domain.model.user.data.UserData;
import com.store.domain.service.email.EmailService;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

public class SendGridMailService implements EmailService {
	private static final Logger logger = Logger.getLogger(SendGridMailService.class.getName());

	private static final String NAME_REPLACEABLE_PATTERN = "%NOMBRE%";
	private static final String VALIDATION_CODE_REPLACEABLE_PATTERN = "%CODIGO%";

	private String genericSenderEmail;
	private String verificationCodeTemplateId;
	private String welcomeTemplateId;
	private String sendgridApiKey;

	@Inject
	public SendGridMailService(@Named("emails.generic.sender") String genericSender,
			@Named("emails.verification.code.templateId") String verificationCodeTemplateId,
			@Named("emails.welcome.templateId") String welcomeTemplateId,
			@Named("emails.sendgrid.apikey") String sendgridApikey) {
		this.genericSenderEmail = genericSender;

		this.verificationCodeTemplateId = verificationCodeTemplateId;
		this.welcomeTemplateId = welcomeTemplateId;

		this.sendgridApiKey = sendgridApikey;
	}

	public void sendTemplateMail(@NonNull TemplateEmailConfiguration configuration) {
		Email from = new Email(configuration.getRecipientEmail());
		Email to = new Email(configuration.getReceiverEmail());

		Personalization personalization = new Personalization();

		for (Entry<String, String> mapEntry : configuration.getSubstitutions().entrySet()) {
			personalization.addSubstitution(mapEntry.getKey(), mapEntry.getValue());
		}

		personalization.addTo(to);

		Mail mail = new Mail();
		mail.setTemplateId(configuration.getTemplateId());
		mail.setFrom(from);
		mail.addPersonalization(personalization);

		if (configuration.getSubject() != null) {
			mail.setSubject(configuration.getSubject());
		}

		send(mail);

	}

	private void send(@NonNull Mail mail) {
		SendGrid sg = new SendGrid(sendgridApiKey);
		Request request = new Request();

		try {
			request.setMethod(Method.POST);
			request.setEndpoint("mail/send");
			request.setBody(mail.build());

			Response response = sg.api(request);

			logger.info("Mail sent!");
			logger.info("------------------------");
			logger.info("Mail service response received: ");
			logger.info("Status Code: " + response.getStatusCode());
			logger.info("Headers: " + response.getHeaders());

			if (!StringUtils.isEmpty(response.getBody())) {
				logger.info("Body: " + response.getBody());
			}

			logger.info("------------------------");
		} catch (IOException e) {
			logger.severe("Unable to send email: " + e.getMessage());

			Writer buffer = new StringWriter();
			PrintWriter pw = new PrintWriter(buffer);
			e.printStackTrace(pw);

			logger.severe(buffer.toString());
		}
	}

	@Override
	public void sendVerificationCodeEmail(@NonNull UserData userData, @NonNull String registrationCode) {
		HashMap<String, String> substitutionMap = new HashMap<String, String>();

		substitutionMap.put(NAME_REPLACEABLE_PATTERN, userData.getFirstName());
		substitutionMap.put(VALIDATION_CODE_REPLACEABLE_PATTERN, registrationCode);

		sendTemplateMail(TemplateEmailConfiguration.builder().recipientEmail(this.genericSenderEmail)
				.receiverEmail(userData.getEmail()).templateId(verificationCodeTemplateId)
				.substitutions(substitutionMap).build());
	}

	@Override
	public void sendWelcomeEmail(@NonNull UserData userData) {
		HashMap<String, String> substitutionMap = new HashMap<String, String>();
		substitutionMap.put(NAME_REPLACEABLE_PATTERN, userData.getFirstName());

		sendTemplateMail(TemplateEmailConfiguration.builder().recipientEmail(this.genericSenderEmail)
				.receiverEmail(userData.getEmail()).templateId(welcomeTemplateId).substitutions(substitutionMap)
				.build());
	}

	@Builder
	@Getter
	static class TemplateEmailConfiguration {
		@NonNull
		private String recipientEmail;
		@NonNull
		private String receiverEmail;
		@NonNull
		private String templateId;
		@NonNull
		private HashMap<String, String> substitutions;

		private String subject;
	}

}
