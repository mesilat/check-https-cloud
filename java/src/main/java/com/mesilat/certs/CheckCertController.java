package com.mesilat.certs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CheckCertController {
    public final static long MS = 24l * 3600l * 1000l;

    @Autowired
    private CheckCertificate service;
    private final ObjectMapper mapper = new ObjectMapper();

    @RequestMapping(value = {"/notAfter"}, method = RequestMethod.GET)
    public ResponseEntity<?> notAfter(@RequestParam("host") String host, @RequestParam(name="port", required=false) Integer port
    ) throws CheckCertificateException {
        ObjectNode node = mapper.createObjectNode();
        Date notAfter = service.getNotAfter(host, port == null? 443: port);
        node.put("expires", notAfter.toString());
        node.put("days", (notAfter.getTime() - System.currentTimeMillis()) / MS);
        return new ResponseEntity<>(node, HttpStatus.OK);
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public void redirectToSwaggerUI(HttpServletRequest req, HttpServletResponse resp) {
        resp.setHeader("Location", String.format("%s/swagger-ui/", req.getContextPath()));
        resp.setStatus(302);
    }
}