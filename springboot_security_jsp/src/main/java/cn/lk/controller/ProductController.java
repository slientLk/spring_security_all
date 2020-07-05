package cn.lk.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/product")
public class ProductController {

    @Secured({"ROLE_ADMIN","ROLE_PRODUCT"})
    @RequestMapping("/findAll")
    public String hello(){
        return "product-list";
    }
}
