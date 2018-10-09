import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.mysql.cj.xdevapi.JsonParser;
import jdk.nashorn.internal.parser.JSONParser;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.*;
import org.eclipse.jetty.util.ajax.JSON;

public class App extends HttpServlet {

    private String API_KEY = "123456";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        String api_key = req.getParameter("api_key");
        if(action != null && !action.isEmpty() && action.equals("developer_ui")){
            View v = new View();
            resp.getWriter().print(v.developer_view());
        }
        else if(action != null && !action.isEmpty() && action.equals("interview_ui")){
            int id = Integer.valueOf(req.getParameter("id"));
            if(id > 0) {
                View v = new View();
                resp.getWriter().print(v.interview_view(id));
            }
            else{
                resp.getWriter().print("Invalid Url!");
            }
        }
        else if(api_key != null && !api_key.isEmpty()) {
            resp.setHeader("Content-Type", "application/json;charset=utf-8");
            if (api_key.equals(this.API_KEY)) {
                if (action != null && !action.isEmpty() && action.equals("retrieve_developer")) {
                    int count = Integer.valueOf(req.getParameter("count"));
                    String plang = req.getParameter("plang");
                    String lang = req.getParameter("lang");
                    if ((lang != null && !lang.isEmpty()) && (plang != null && !plang.isEmpty()) && count > 0) {
                        try {
                            Model m = new Model();
                            String response = m.search_developer(lang, plang, count);
                            resp.getWriter().print(response);
                        } catch (Exception e) {
                            resp.getWriter().print("[{\"status\":\"failed\",\"error\":\"System error While connecting Database:" + e.getMessage() + "\"}]");
                        }
                    } else if ((lang != null && !lang.isEmpty()) && count > 0) {
                        try {
                            Model m = new Model();
                            String response = m.search_lang_developer(lang, count);
                            resp.getWriter().print(response);
                        } catch (Exception e) {
                            resp.getWriter().print("[{\"status\":\"failed\",\"error\":\"System error While connecting Database:" + e.getMessage() + "\"}]");
                        }
                    } else if ((plang != null && !plang.isEmpty()) && count > 0) {
                        try {
                            Model m = new Model();
                            String response = m.search_plang_developer(plang, count);
                            resp.getWriter().print(response);
                        } catch (Exception e) {
                            resp.getWriter().print("[{\"status\":\"failed\",\"error\":\"System error While connecting Database:" + e.getMessage() + "\"}]");
                        }
                    } else if (count > 0) {
                        try {
                            Model m = new Model();
                            String response = m.retrieve_developer(count);
                            resp.getWriter().print(response);
                        } catch (Exception e) {
                            resp.getWriter().print("[{\"status\":\"failed\",\"error\":\"System error While connecting Database:" + e.getMessage() + "\"}]");
                        }
                    } else {
                        resp.getWriter().print("[{\"status\":\"failed\",\"error\":\"Invalid parameter list!\"}]");
                    }
                } else if (action != null && !action.isEmpty() && action.equals("retrieve_interviews")) {
                    int count = Integer.valueOf(req.getParameter("count"));
                    int id = Integer.valueOf(req.getParameter("id"));
                    String sort = req.getParameter("sort");
                    if (id > 0 && (sort != null && !sort.isEmpty()) && count > 0) {
                        try {
                            Model m = new Model();
                            String response = m.retrieve_interviews(count, id, sort);
                            resp.getWriter().print(response);
                        } catch (Exception e) {
                            resp.getWriter().print("[{\"status\":\"failed\",\"error\":\"System error While connecting Database:" + e.getMessage() + "\"}]");
                        }
                    } else if (id > 0 && count > 0) {
                        try {
                            Model m = new Model();
                            String response = m.retrieve_interviews(count, id);
                            resp.getWriter().print(response);
                        } catch (Exception e) {
                            resp.getWriter().print("[{\"status\":\"failed\",\"error\":\"System error While connecting Database:" + e.getMessage() + "\"}]");
                        }
                    } else {
                        resp.getWriter().print("[{\"status\":\"failed\",\"error\":\"Invalid parameter list!\"}]");
                    }
                } else {
                    resp.getWriter().print("[{\"status\":\"failed\",\"error\":\"Invalid URL!\"}]");
                }
            } else {
                resp.getWriter().print("[{\"status\":\"failed\",\"error\":\"Invalid API KEY\"}]");
            }
        }
        else{
            resp.getWriter().print("Welcome to Developer Center!");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException{
        Map<String, String> dataMap = Utils.getParameterMap(req);

        String api_key = dataMap.get("api_key");
        if(api_key != null && !api_key.isEmpty()) {
            resp.setHeader("Content-Type", "application/json;charset=utf-8");
            if(api_key.equals(this.API_KEY)) {
                String action = dataMap.get("action");
                if (action != null && !action.isEmpty() && action.equals("create_developer")) {
                    String email = dataMap.get("email");
                    String plang = dataMap.get("plang");
                    String lang = dataMap.get("lang");

                    if ((email != null && !email.isEmpty()) && (plang != null && !plang.isEmpty()) && (lang != null && !lang.isEmpty())) {
                        try {
                            Model m = new Model();
                            String response = m.create_developer(email, plang, lang);
                            resp.getWriter().print(response);
                        } catch (Exception e) {
                            resp.getWriter().print("[{\"status\":\"failed\",\"error\":\"System error While connecting Database\"}]");
                        }
                    } else {
                        resp.getWriter().print("[{\"status\":\"failed\",\"error\":\"Invalid parameter list!\"}]");
                    }
                }
                else if (action != null && !action.isEmpty() && action.equals("add_interview")) {
                    int id = Integer.valueOf(dataMap.get("id"));
                    int score = Integer.valueOf(dataMap.get("score"));
                    String comment = dataMap.get("comment");

                    if (id>0 && score>-1) {
                        try {
                            Model m = new Model();
                            String response = m.add_interview(id, score, comment);

                            resp.getWriter().print(response);
                        } catch (Exception e) {
                            resp.getWriter().print("[{\"status\":\"failed\",\"error\":\"System error While connecting Database\"}]");
                        }
                    } else {
                        resp.getWriter().print("[{\"status\":\"failed\",\"error\":\"Invalid parameter list!\"}]");
                    }
                }
                else {
                    resp.getWriter().print("[{\"status\":\"failed\",\"error\":\"Invalid URL!\"}]");
                }
            }
            else{
                resp.getWriter().print("[{\"status\":\"failed\",\"error\":\"Invalid API KEY\"}]");
            }
        }
        else{
            resp.getWriter().print("Welcome to Developer Center!");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException{
        Map<String, String> dataMap = Utils.getParameterMap(req);

        String api_key = dataMap.get("api_key");
        if(api_key != null && !api_key.isEmpty()) {
            resp.setHeader("Content-Type", "application/json;charset=utf-8");
            if(api_key.equals(this.API_KEY)) {
                String action = dataMap.get("action");
                if (action != null && !action.isEmpty() && action.equals("delete_developer")) {
                    int id = Integer.valueOf(dataMap.get("id"));
                    if (id > 0) {
                        try {
                            Model m = new Model();
                            String response = m.delete_developer(id);
                            resp.getWriter().print(response);
                        } catch (Exception e) {
                            resp.getWriter().print("[{\"status\":\"failed\",\"error\":\"System error While connecting Database\"}]");
                        }
                    } else {
                        resp.getWriter().print("[{\"status\":\"failed\",\"error\":\"Invalid parameter list!\"}]");
                    }
                }
                else if (action != null && !action.isEmpty() && action.equals("delete_interview")) {
                    int id = Integer.valueOf(dataMap.get("id"));
                    if (id > 0) {
                        try {
                            Model m = new Model();
                            String response = m.delete_interview(id);
                            resp.getWriter().print(response);
                        } catch (Exception e) {
                            resp.getWriter().print("[{\"status\":\"failed\",\"error\":\"System error While connecting Database\"}]");
                        }
                    } else {
                        resp.getWriter().print("[{\"status\":\"failed\",\"error\":\"Invalid parameter list!\"}]");
                    }
                }
                else{
                    resp.getWriter().print("[{\"status\":\"failed\",\"error\":\"Invalid URL!\"}]");
                }
            }
            else{
                resp.getWriter().print("[{\"status\":\"failed\",\"error\":\"Invalid API KEY\"}]");
            }
        }
        else{
            resp.getWriter().print("Welcome to Developer Center!");
        }
    }

    public static void main(String[] args) throws Exception{

        int port = (System.getenv("PORT") == null) ? 80 : Integer.valueOf(System.getenv("PORT"));
        Server server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        context.addServlet(new ServletHolder(new App()),"/*");
        server.start();
        server.join();
    }
}