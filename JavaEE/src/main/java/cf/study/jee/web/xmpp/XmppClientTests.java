package cf.study.jee.web.xmpp;

import javax.net.ssl.SSLContext;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Test;

import rocks.xmpp.core.session.TcpConnectionConfiguration;
import rocks.xmpp.core.session.XmppClient;
import rocks.xmpp.core.stanza.model.Presence;

public class XmppClientTests {

	@Test
	public void testXmppClientConn() throws Exception {
		SSLContext sslCtx = SSLContext.getDefault();
		TcpConnectionConfiguration tcpCfg = TcpConnectionConfiguration.builder().hostname("atkins.thenetcircle.lab").port(5222).sslContext(sslCtx).keepAliveInterval(5).build();

		try (XmppClient xmppClient = XmppClient.create("poppen.xmpp.lab", tcpCfg)) {

			xmppClient.addInboundPresenceListener(e -> {
				Presence p = e.getPresence();
				System.out.println(ToStringBuilder.reflectionToString(p, ToStringStyle.MULTI_LINE_STYLE));
			});
		}
	}
}
