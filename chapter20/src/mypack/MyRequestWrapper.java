package mypack;
import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;

public class MyRequestWrapper extends HttpServletRequestWrapper {
  public MyRequestWrapper(HttpServletRequest request) {
    super(request);
  }

  public String getParameter(String name){
    String value=super.getParameter(name);
    if(value==null){
      value="none";
    }else{
      //����������еġ�-���滻Ϊ��/��
      value=value.replaceAll("-","/");
    }
    return value;
  }
}







/****************************************************
 * ���ߣ�������                                     *
 * ��Դ��<<Tomcat��Java Web�����������>>           *
 * ����֧����ַ��www.javathinker.net                *
 ***************************************************/
