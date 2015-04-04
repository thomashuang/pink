import com.pink.util.mail.{Mail, Mailer}

object Hi {

  def main(args: Array[String]) = {
    
    Mailer.setup("smtp.exmail.qq.com", 465, "test@qq.com", "123", true)
    Mailer send  new Mail (
      from = "test@qq.com" -> "John Smith",
      to = Seq("test@qq.com"),
      subject = "Test",
      message = "Hello Ni hao"
    )

  }
}