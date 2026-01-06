package mypack;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.*;

@WebServlet(name="AsyncServlet",
            urlPatterns="/async",
            asyncSupported=true)

public class AsyncServlet extends HttpServlet{

  public void service(HttpServletRequest request,
              HttpServletResponse response)
              throws ServletException,IOException{  

     response.setContentType("text/plain;charset=GBK");
     AsyncContext asyncContext = request.getAsyncContext();
     asyncContext.start(new MyTask(asyncContext));

  }

  class MyTask implements Runnable{
    private AsyncContext asyncContext;

    public MyTask(AsyncContext asyncContext){
      this.asyncContext = asyncContext;
    }

    public void run(){
      try{
        //˯��5�룬ģ��ܺ�ʱ��һ��ҵ�����
        Thread.sleep(5*1000);
        asyncContext.getResponse()
                  .getWriter()
                  .write("�����õ���!");   
        asyncContext.complete();
      }catch(Exception e){e.printStackTrace();}
    }
  }
}