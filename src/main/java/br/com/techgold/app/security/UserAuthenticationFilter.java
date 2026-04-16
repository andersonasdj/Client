package br.com.techgold.app.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.techgold.app.model.Cliente;
import br.com.techgold.app.model.CustomUserDetails;
import br.com.techgold.app.services.ClienteService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class UserAuthenticationFilter extends OncePerRequestFilter {

	@Autowired private ClienteService clienteService;
	
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
	        throws ServletException, IOException {

	    boolean isLoginPost = "/cliente/login".equals(request.getRequestURI().trim())
	            && "POST".equalsIgnoreCase(request.getMethod().trim());

	    if (isLoginPost) {

	        filterChain.doFilter(request, response);

	        var auth = SecurityContextHolder.getContext().getAuthentication();
	        
	        if (auth == null || !auth.isAuthenticated()) {
	            return;
	        }

	        if (auth != null && auth.getPrincipal() != null) {

	            Object principal = auth.getPrincipal();

	            // ===================== CLIENTE =====================
	            if (principal instanceof Cliente cliente) {

	                cliente = clienteService.buscaPorNome(cliente.getNomeCliente());

	                System.out.println("CLIENTE LOGADO: " + cliente.getNomeCliente());

	                clienteService.atualizaIpLogin(
	                        cliente,
	                        request.getRemoteHost(),
	                        request.getLocale().getCountry()
	                );
	            }

	            // ===================== CUSTOM USER (COLABORADOR) =====================
	            else if (principal instanceof CustomUserDetails customUser) {

	                Object entidade = customUser.getEntidade();

	                if (entidade instanceof Cliente cliente) {

	                    cliente = clienteService.buscaPorNome(cliente.getNomeCliente());

	                    System.out.println("CLIENTE LOGADO (CUSTOM): " + cliente.getNomeCliente());

	                    clienteService.atualizaIpLogin(
	                            cliente,
	                            request.getRemoteHost(),
	                            request.getLocale().getCountry()
	                    );
	                }

	                // AQUI você pode tratar colaborador se quiser
	                else {
	                    System.out.println("COLABORADOR LOGADO: " + customUser.getUsername());
	                }
	            }

	            // ===================== FALLBACK =====================
	            else {
	                String user = request.getParameter("username");
	                System.out.println(user + " - USUARIO NÃO IDENTIFICADO NO FILTER!");
	                SecurityContextHolder.clearContext();
	            }

	        } else {
	            String user = request.getParameter("username");
	            System.out.println(user + " - USUARIO SEM SESSÃO PASSOU NO FILTRO!");
	            SecurityContextHolder.clearContext();
	        }

	    } else {
	        filterChain.doFilter(request, response);
	    }
	}
	
	
	
	
	
	
//	@Override
//	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//		
//		boolean isLoginPost = "/cliente/login".equals(request.getRequestURI().trim()) && "POST".equalsIgnoreCase(request.getMethod().trim());
//		
//		if(isLoginPost) {
//			
//			filterChain.doFilter(request, response);
//			var auth = SecurityContextHolder.getContext().getAuthentication();
//			
//			if(auth != null && auth.getPrincipal() instanceof Cliente)  {
//				Cliente cliente = clienteService.buscaPorNome(((Cliente) auth.getPrincipal()).getNomeCliente());
//				System.out.println("CLIENTE LOGADO: " + cliente.getNomeCliente());
//				clienteService.atualizaIpLogin(cliente, request.getRemoteHost(),request.getLocale().getCountry());
//			}else {
//				String user = request.getParameter("username");
//				System.out.println(user + " - USUARIO SEM SESSÃO PASSOU NO FILTRO!");
//				SecurityContextHolder.clearContext();
//			}
//		}else {
//			filterChain.doFilter(request, response);
//		}
//		
//	}
	
	String getBrowser(HttpServletRequest request) {
		
		String  browserDetails  =   request.getHeader("User-Agent");
        String  userAgent       =   browserDetails;
        String  user            =   userAgent.toLowerCase();
        String os = "";
        String browser = "";

        //=================OS=======================
         if (userAgent.toLowerCase().indexOf("windows") >= 0 )
         {
             os = "Windows";
         } else if(userAgent.toLowerCase().indexOf("mac") >= 0)
         {
             os = "Mac";
         } else if(userAgent.toLowerCase().indexOf("x11") >= 0)
         {
             os = "Unix";
         } else if(userAgent.toLowerCase().indexOf("android") >= 0)
         {
             os = "Android";
         } else if(userAgent.toLowerCase().indexOf("iphone") >= 0)
         {
             os = "IPhone";
         }else{
             os = "UnKnown, More-Info: "+userAgent;
         }
         //===============Browser===========================
        if (user.contains("msie"))
        {
            String substring=userAgent.substring(userAgent.indexOf("MSIE")).split(";")[0];
            browser=substring.split(" ")[0].replace("MSIE", "IE")+"-"+substring.split(" ")[1];
        } else if (user.contains("safari") && user.contains("version"))
        {
            browser=(userAgent.substring(userAgent.indexOf("Safari")).split(" ")[0]).split("/")[0]+"-"+(userAgent.substring(userAgent.indexOf("Version")).split(" ")[0]).split("/")[1];
        } else if ( user.contains("opr") || user.contains("opera"))
        {
            if(user.contains("opera"))
                browser=(userAgent.substring(userAgent.indexOf("Opera")).split(" ")[0]).split("/")[0]+"-"+(userAgent.substring(userAgent.indexOf("Version")).split(" ")[0]).split("/")[1];
            else if(user.contains("opr"))
                browser=((userAgent.substring(userAgent.indexOf("OPR")).split(" ")[0]).replace("/", "-")).replace("OPR", "Opera");
        } else if (user.contains("chrome"))
        {
            browser=(userAgent.substring(userAgent.indexOf("Chrome")).split(" ")[0]).replace("/", "-");
        } else if ((user.indexOf("mozilla/7.0") > -1) || (user.indexOf("netscape6") != -1)  || (user.indexOf("mozilla/4.7") != -1) || (user.indexOf("mozilla/4.78") != -1) || (user.indexOf("mozilla/4.08") != -1) || (user.indexOf("mozilla/3") != -1) )
        {
            //browser=(userAgent.substring(userAgent.indexOf("MSIE")).split(" ")[0]).replace("/", "-");
            browser = "Netscape-?";
                  
        } else if (user.contains("firefox"))
        {
            browser=(userAgent.substring(userAgent.indexOf("Firefox")).split(" ")[0]).replace("/", "-");
        } else if(user.contains("rv"))
        {
            browser="IE-" + user.substring(user.indexOf("rv") + 3, user.indexOf(")"));
        } else
        {
            browser = "UnKnown, More-Info: "+userAgent;
        }
        
        return os + " - " + browser;
	}

}
