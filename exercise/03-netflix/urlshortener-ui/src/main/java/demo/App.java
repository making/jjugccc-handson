package demo;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.Collections;

@EnableAutoConfiguration
@ComponentScan
@Controller
@Configuration
public class App {
    @Autowired
    RestTemplate restTemplate;
    @Value("${urlshorten.api.url:http://localhost:8081}")
    String apiUrl;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @ModelAttribute
    ShortenForm setupForm() {
        return new ShortenForm();
    }

    @RequestMapping("/")
    String home() {
        return "index";
    }

    @RequestMapping(value = "shorten", method = RequestMethod.POST)
    String shorten(@Validated ShortenForm form, BindingResult result, RedirectAttributes attributes) throws IOException {
        if (result.hasErrors()) {
            return "index";
        }
        URI target = URI.create(apiUrl + "/" + URLDecoder.decode(form.getUrl(), "UTF-8")); // //が/に短縮されるのを防止
        String shortenUrl = restTemplate.postForObject(target, null, String.class);
        attributes.addFlashAttribute("shortenUrl", shortenUrl);
        return "redirect:/";
    }

    @RequestMapping(value = "/{hash}", method = RequestMethod.GET)
    String redirect(@PathVariable String hash) throws IOException {
        // Tips: プレースホルダを利用するとURLエンコーディングが行われる。shortenメソッド内ではエンコーディング不要だった。
        String url = restTemplate.getForObject(apiUrl + "/{hash}", String.class, Collections.singletonMap("hash", hash));
        return "redirect:" + url;
    }

    @Bean
    RestTemplate restTemplate() {
        // TODO spring-cloud-netflixに変える
        return new RestTemplate();
    }
}

class ShortenForm {
    @NotEmpty
    @URL
    private String url;

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}