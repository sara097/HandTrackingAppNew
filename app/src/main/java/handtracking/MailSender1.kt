package com.example.smtpemailsender

import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.security.AccessController
import java.security.PrivilegedAction
import java.security.Provider
import java.security.Security
import java.util.*
import javax.activation.CommandMap
import javax.activation.DataHandler
import javax.activation.DataSource
import javax.activation.MailcapCommandMap
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

class GMailSender(private val user: String, private val password: String) : Authenticator() {
    private val mailhost = "smtp.gmail.com"
    private val session: Session

    companion object {
        init {
            Security.addProvider(JSSEProvider())
        }
    }

    override fun getPasswordAuthentication(): PasswordAuthentication {
        return PasswordAuthentication(user, password)
    }

    @Synchronized
    @Throws(java.lang.Exception::class)
    fun sendMail(subject: String?, body: String, sender: String?, recipients: String) {
        try {
            println("???")
            val message = MimeMessage(session)
            val handler = DataHandler(ByteArrayDataSource(body.toByteArray(), "text/plain"))
            message.sender = InternetAddress(sender)
            message.subject = subject
            message.dataHandler = handler
            if (recipients.indexOf(',') > 0) message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients)) else message.setRecipient(Message.RecipientType.TO, InternetAddress(recipients))
            Transport.send(message)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    inner class ByteArrayDataSource : DataSource {
        private var data: ByteArray
        private var type: String? = null

        constructor(data: ByteArray, type: String?) : super() {
            this.data = data
            this.type = type
        }

        constructor(data: ByteArray) : super() {
            this.data = data
        }

        fun setType(type: String?) {
            this.type = type
        }


        override fun getName(): String {
            return "ByteArrayDataSource"
        }

        override fun getOutputStream(): OutputStream {
            throw IOException("Not Supported")
        }

        override fun getInputStream(): InputStream {
            return ByteArrayInputStream(data)
        }

        override fun getContentType(): String {
            return "application/octet-stream"
        }
    }

    init {
        val props = Properties()
        props.setProperty("mail.transport.protocol", "smtp")
        props.setProperty("mail.host", mailhost)
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.port"] = "465"
        props["mail.smtp.socketFactory.port"] = "465"
        props["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
        props["mail.smtp.socketFactory.fallback"] = "false"
        props.setProperty("mail.smtp.quitwait", "false")
        session = Session.getDefaultInstance(props, GMailAuthenticator("sarciasara1234@gmail.com", "Romeoijulia6"))
        session.debug = true
    }
}

internal class GMailAuthenticator(var user: String, var pw: String) : Authenticator() {
    override fun getPasswordAuthentication(): PasswordAuthentication {
        return PasswordAuthentication(user, pw)
    }

}


class JSSEProvider : Provider("HarmonyJSSE", 1.0, "Harmony JSSE Provider") {
    init {
        AccessController.doPrivileged(PrivilegedAction<Void?> {
            put("SSLContext.TLS",
                    "org.apache.harmony.xnet.provider.jsse.SSLContextImpl")
            put("Alg.Alias.SSLContext.TLSv1", "TLS")
            put("KeyManagerFactory.X509",
                    "org.apache.harmony.xnet.provider.jsse.KeyManagerFactoryImpl")
            put("TrustManagerFactory.X509",
                    "org.apache.harmony.xnet.provider.jsse.TrustManagerFactoryImpl")
            null
        })
    }
}


class Mail(
        var user: String = "",
        var pass: String = ""
) : Authenticator() {
    var to: Array<String> = arrayOf()
    var from = ""
    val _port = "587"
    private val _sport = "465"
    private val _host = "smtp.gmail.com"
    var _subject = ""

    // the getters and setters
    var body = ""
    private val _auth = true
    private val _debuggable = false
    private val _multipart: Multipart


    @Throws(Exception::class)
    fun send(): Boolean {
        val props = _setProperties()
        return if (user != "" && pass != "" && to.size > 0 && from != "" &&
                _subject != "" && body != "") {
            val session = Session.getInstance(props, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication("sarciasara1234@gmail", "Romeoijulia6")
                }
            })
            val authentication = SMTPAuthenticator()
            val msg: Message = MimeMessage(Session
                    .getDefaultInstance(props, authentication))
            msg.setFrom(InternetAddress(from))
            val addressTo = arrayOfNulls<InternetAddress>(to.size)
            for (i in to.indices) {
                addressTo[i] = InternetAddress(to[i])
            }
            msg.setRecipients(MimeMessage.RecipientType.TO, addressTo)
            msg.subject = _subject
            msg.sentDate = Date()

// setup message body
            val messageBodyPart: BodyPart = MimeBodyPart()
            messageBodyPart.setText(body)
            _multipart.addBodyPart(messageBodyPart)
            msg.setContent(_multipart)
            val protocol = "smtp"
            props["mail.$protocol.auth"] = "true"
            val t = session.getTransport(protocol)
            try {
                t.connect("smtp.gmail.com", "sarciasara1234@gmail", "Romeoijulia6")
                t.sendMessage(msg, msg.allRecipients)
            } finally {
                t.close()
            }
            true
        } else {
            false
        }
    }

    public override fun getPasswordAuthentication(): PasswordAuthentication {
        return PasswordAuthentication(user, pass)
    }

    private fun _setProperties(): Properties {
        val props = Properties()
        props["mail.smtp.host"] = _host
        //if (_debuggable) {
        props["mail.debug"] = "true"
        // }
        //if (_auth) {
        props["mail.smtp.auth"] = "true"
        // }
        props["mail.smtp.port"] = _port
        props["mail.smtp.starttls.enable"] = "true"
        props["mail.smtp.host"] = "smtp.gmail.com"
        props["mail.smtp.ssl.enable"] = true
        props["mail.smtp.tls.enable"] = true
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        //props.put("mail.smtp.auth", "true");
        return props
    }


    init {
        _multipart = MimeMultipart()
        val mc = CommandMap.getDefaultCommandMap() as MailcapCommandMap
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html")
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml")
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain")
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed")
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822")
        CommandMap.setDefaultCommandMap(mc)
    }
}


class SMTPAuthenticator : Authenticator() {
    public override fun getPasswordAuthentication(): PasswordAuthentication? {
        val username = "sarciasara1234@gmail"
        val password = "Romeoijulia6"
        return if (username != null && username.length > 0 && password != null && password.length > 0)
            PasswordAuthentication(username, password)
        else null
    }
}