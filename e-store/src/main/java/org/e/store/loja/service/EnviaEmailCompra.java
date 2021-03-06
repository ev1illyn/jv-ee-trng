package org.e.store.loja.service;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.e.store.loja.daos.CompraDao;
import org.e.store.loja.infra.MailSender;
import org.e.store.loja.models.Compra;

/* Anotação para capturar/ouvir a mensagem para depois enviá-la
 * propertyValue é aquele que vai ser ouvido*/
@MessageDriven(activationConfig = {
		@ActivationConfigProperty(
				propertyName="destinationLookup",
				propertyValue="java:/jms/topics/CarrinhoComprasTopico")
})
public class EnviaEmailCompra implements MessageListener {

	@Inject
	private MailSender mailSender;

	@Inject
	private CompraDao compraDao;

	@Override
	public void onMessage(Message message) {

		try {
			TextMessage textMessage = (TextMessage) message;

			Compra compra = compraDao.buscaPorUuid(textMessage.getText());

			String messageBody = compra.getUsuario().getNome() + ", sua compra foi realizada com sucesso, com número de pedido: " + compra.getUuid();

			mailSender.send("compras@e-store.com.br", compra.getUsuario().getEmail(), "Sua compra na E-Store",
					messageBody);

		} catch (JMSException e) {
			e.printStackTrace();
		}

	}
}