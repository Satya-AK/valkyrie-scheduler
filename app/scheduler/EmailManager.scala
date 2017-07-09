package scheduler

import java.util.Properties
import javax.activation.DataHandler
import javax.mail._
import javax.mail.internet.{InternetAddress, MimeBodyPart, MimeMessage, MimeMultipart}
import javax.mail.util.ByteArrayDataSource
import model.{AppEmailConnection, AppGroup, AppInstance}


/**
  * Created by chlr on 7/7/17.
  */
class EmailManager(emailConnection: AppEmailConnection) {


  /**
    * send email
    * @param appInstance
    * @param jobName
    * @param appGroup
    */
  def send(appInstance: AppInstance, jobName: String, appGroup: AppGroup,
           stdout: Option[String], stderr: Option[String]) = {
    val composer = new EmailComposer(appInstance, jobName, appGroup.groupName)
    val message = new MimeMessage(getSession)
    message.setFrom(new InternetAddress(emailConnection.fromEmail))
    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(appGroup.groupEmail).toArray[Address])
    message.setSubject(composer.subject)
    val multipart = new MimeMultipart()
    val messageBodyPart = new MimeBodyPart()
    messageBodyPart.setContent(composer.template, "text/html")
    multipart.addBodyPart(messageBodyPart)
    stdout.foreach(x => multipart.addBodyPart(attachmentBodyPart(x, "stdout.log")))
    stderr.foreach(x => multipart.addBodyPart(attachmentBodyPart(x, "stderr.log")))
    message.setContent(multipart)
    Transport.send(message)
    println("")
  }


  private def attachmentBodyPart(data: String, fileName: String) = {
    val part = new MimeBodyPart()
    part.setDataHandler(new DataHandler(new ByteArrayDataSource(data, "text/plain")))
    part.setDisposition("attachment")
    part.setFileName(fileName)
    part.setHeader("Content-Transfer-Encoding", "base64")
    part.setHeader("Content-type", "text/plain; charset=utf-8")
    part
  }


  /**
    * get mailx session
    * @return
    */
  private def getSession = {
    val props = new Properties()
    props.put("mail.smtp.auth", "true")
    props.put("mail.smtp.starttls.enable", emailConnection.tls.toString)
    props.put("mail.smtp.host", emailConnection.hostname)
    props.put("mail.smtp.port", emailConnection.port.toString)
    Session.getInstance(props, new javax.mail.Authenticator() {
      override protected def getPasswordAuthentication =
        new PasswordAuthentication(emailConnection.username, emailConnection.password)
    })
  }

}
