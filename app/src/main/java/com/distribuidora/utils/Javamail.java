package com.distribuidora.utils;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import android.content.Context;

public class Javamail {

	private String mensaje;
	private String asunto;
	private String destinatario;
	private String remitente;
	private String passremitente;
	
	public void enviar(Context ctn) {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		Preferencias preferencias = new Preferencias(ctn);

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(remitente, passremitente);
			}
		});

		try {
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(remitente, "Control Distribuidora Android"));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(destinatario, "Vendedor "+preferencias.getIdVendedor()));
			msg.setSubject(asunto);
			msg.setText(mensaje);
			Transport.send(msg);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.out.println("Error:" + e.getMessage());
		} catch (MessagingException e) {
			e.printStackTrace();
			System.out.println("Error:" + e.getMessage());
		}
	}
	
	
	public void enviar(Multipart multipart, Context ctn) {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		Preferencias preferencias = new Preferencias(ctn);

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(remitente, passremitente);
			}
		});

		try {
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(remitente, "Mantenimiento"));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(destinatario, "Vendedor "+preferencias.getIdVendedor()));
			msg.setSubject(asunto);
			msg.setContent(multipart);
			Transport.send(msg);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.out.println("Error:" + e.getMessage());
		} catch (MessagingException e) {
			e.printStackTrace();
			System.out.println("Error:" + e.getMessage());
		}
	}

	public String getAsunto() {
		return asunto;
	}

	public void setAsunto(String asunto) {
		this.asunto = asunto;
	}

	public String getDestinatario() {
		return destinatario;
	}

	public void setDestinatario(String destinatario) {
		this.destinatario = destinatario;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public String getRemitente() {
		return remitente;
	}

	public void setRemitente(String remitente, String contrasenia) {
		this.remitente = remitente;
		this.passremitente = contrasenia;
	}
}
