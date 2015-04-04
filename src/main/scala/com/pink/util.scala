package com.pink.util.mail
 
import org.apache.commons.mail._
 
sealed abstract class MailType
case object Plain extends MailType
case object Rich extends MailType
case object MultiPart extends MailType


case class Mail(
  from: (String, String), // (email -> name)
  to: Seq[String],
  cc: Seq[String] = Seq.empty,
  bcc: Seq[String] = Seq.empty,
  subject: String,
  message: String,
  richMessage: Option[String] = None,
  attachment: Option[(java.io.File)] = None
)
 
object Mailer {

  private var _username = "" 
  private var _password = ""
  private var _host = ""
  private var _port = 0 : Integer
  private var _useSSL = false
  private var _needAuth = false
  private var _debug = false
 
  def setup(host : String, port : Integer = 443, username : String = "", password : String = "", 
    ssl : Boolean = false, debug : Boolean = false) {
    _host = host 
    _port = port
    if ( ! (username.length == 0 && password.length == 0)) {
      _username = username
      _password = password
      _needAuth = true
 
    }
    _useSSL = ssl
    _debug = debug
 
  }
 
 
  private def setupConfig(commonsMail: Email)  {
    
    commonsMail.setHostName(_host)
    commonsMail.setSmtpPort(_port)
    commonsMail.setDebug(_debug)
    if (_useSSL){
      commonsMail.setSSLOnConnect(true)
    } 

    if (_needAuth)
    {
      commonsMail.setAuthenticator(new DefaultAuthenticator(_username, _password))
    }
  
  }
 
 
  def send(mail: Mail)  {
    val format =
      if (mail.attachment.isDefined) MultiPart
      else if (mail.richMessage.isDefined) Rich
      else Plain
 
    val commonsMail: Email = format match {
      case Plain => new SimpleEmail().setMsg(mail.message)
      case Rich => new HtmlEmail().setHtmlMsg(mail.richMessage.get).setTextMsg(mail.message)
      case MultiPart => {
        val attachment = new EmailAttachment()
        attachment.setPath(mail.attachment.get.getAbsolutePath)
        attachment.setDisposition(EmailAttachment.ATTACHMENT)
        attachment.setName(mail.attachment.get.getName)
        new MultiPartEmail().attach(attachment).setMsg(mail.message)
      }
    }

    setupConfig(commonsMail)
    mail.to foreach (commonsMail.addTo(_))
    mail.cc foreach (commonsMail.addCc(_))
    mail.bcc foreach (commonsMail.addBcc(_))
 
    
    commonsMail.
      setFrom(mail.from._1, mail.from._2).
      setSubject(mail.subject).
      send()
  }
}