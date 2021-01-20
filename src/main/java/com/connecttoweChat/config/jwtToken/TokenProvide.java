package com.connecttoweChat.config.jwtToken;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import sun.misc.BASE64Encoder;

import javax.annotation.PostConstruct;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TokenProvide {
    public static final String AUTHORIZATION_HEADER = "Authorization";

    public static final String WEB_COOKIE_NAME = "web_cookie";

    private final String AUTH = "AUTH";

    private BASE64Encoder base64Encoder = new BASE64Encoder();

    private Key key;

    private long tokenValidityInMilliseconds;

    private static String PRIVATE_KEY =
            "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCtpaRsXJJZ5maF\n" +
                    "GCr2zIwcSqbYagRhtAa7xCKhqgaF3SuB9UhCT7GML32UwGrS1ZhukuIpJ3idIQPe\n" +
                    "vMdFlgyoJj4a4ORg4KEQLGeLPYgWjzEdKyuntwnTBU8wwYHDhzmcns7TnOtONEZM\n" +
                    "BJ14R8ZWk8uLKbJZ3KIOOYWcFaYzEQ4TPGTm4iE61zNFM+wOWLL7l9C7od95xtnH\n" +
                    "FsHShmr6yjC5eZq53xkUUp+hI6FjrHL1BZRro1TVr5TePgWvV7Z5RIJ6DVmwv25I\n" +
                    "NolaZ23BNw+0j3H4mKZFEFrni+Cvw3JYXBk0gh6YgpTAi3nMzgk2Er6ke/4hCuRU\n" +
                    "Ck3aD08FAgEDAoIBAHPDwvLoYZFERFi6x08zCBLcbzrxWEEiryfYFxZxWa6Tclaj\n" +
                    "hYGKdl10/mMq8eHjuvRh7Btvpb4WApR92i5kCHAZfryV7ZXrFgrIRQd+Wrm0y2jH\n" +
                    "cm/PW+IDiiCBAS0E0RMUieJonN7NhDKtvlAv2Y8NMlzGduaTFrQmWRK5GXdfmpoX\n" +
                    "PA+C9rjTTnnVq7D56zZuFimKs8v7tGwseaTEXPCfE3SIQH0VVZUxKPSJCHBwel5Q\n" +
                    "NcIkoRHou9NJ9G3hsVVeBCkpaHYaaw5mVtIaomGYgLR/VFymziPFuc1wLPTLMY6s\n" +
                    "/8Tv40IgIx4czU81kFDy+ppIZYviMe3MrvgHv3MCgYEA3UUkpQtJgE+Wx08z9DN1\n" +
                    "Q36jblOQgtflIQJ1rP2ni9JDod84n5BlAr+Cl/QSChvFjGwAoOISNEAhv2I+GnzO\n" +
                    "M3zGJ3xYbtKgDKjzoOh+RK0h/4pQDpnLHdRRor/ewaSStOG+ORF3iwWR0Tc6e0Kf\n" +
                    "OxP8dayX4kxyZlp086leOWMCgYEAyOb05cRULtYDdj8/dlFsjqtPQSnBTu/oKiJe\n" +
                    "Xl24U7+X8at0mm3aFfUGSj5ECp8+LvuMouZaLvqwu78Q/I22+PxxVL/2ziz3wQmp\n" +
                    "ZjbBNYGwTxuAZNPTUzsWIXOE25v/8yCaYHuivq/AjtwlypNedaip0RB3g4LFMxTi\n" +
                    "U7yldncCgYEAk4Ntw1zbqt+52jTNTXejglRs9De1rI/uFgGjyKkaXTbXwT97FQru\n" +
                    "AdUBuqK2sWfZCEgAa0FheCrBKkF+vFM0IlMuxP2QSeHACHCia0Wpgx4Wqlw1XxEy\n" +
                    "E+LhFyqUgRhhzevUJgulB1kL4M98UixqJ2KoTnMP7DL27ub4onDpe5cCgYEAhe9N\n" +
                    "7oLiyeQCTtTU+YudtHI01hvWNJ/wHBbplD564n+6oRz4ZvPmuU4EMX7YBxTUH1Jd\n" +
                    "we7myfx10n9gqF55+1L2OH/53silK1vGRCSAzlZ1ihJVmI03jNIOwPet571VTMBm\n" +
                    "6v0XKcqAXz1uhwzpo8XGi2BPrQHYzLiW4n3DpE8CgYEAsU+2SnUj1+qu6mgvK5E6\n" +
                    "Prem0yz5Yi/VAxOBOfyrb7UIuN6WPkO1zUSgDYBHkxmRuluw7PwohzfhqgFVSddp\n" +
                    "Tnpv6Ky0Y58eeFx9ECeMsZqfYgvz/RZpIsoTphvujKpXe7Zv27IMIxIfTckGRMt4\n" +
                    "uj6l18eUdGFWqKRR+99zhsE=";

    @PostConstruct
    public void init(){
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(PRIVATE_KEY);
        this.key = new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS256.getJcaName());
        this.tokenValidityInMilliseconds = 60 * 120l;
    }

    public String createToken(Authentication authenticate){
        String authorities = authenticate.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        Date nowDate = new Date();
        long now = nowDate.getTime();
        Date validity = new Date(now + this.tokenValidityInMilliseconds * 1000);
        return Jwts.builder()
                .setSubject(authenticate.getName())
                .claim(AUTH, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setIssuedAt(nowDate)
                .setExpiration(validity)
                .compact();
    }

    public boolean verificationToken(String jwt){
        Claims body = null;
        try {
            body = Jwts.parser().setSigningKey(key).parseClaimsJws(jwt).getBody();

            List<SimpleGrantedAuthority> authority = Arrays.stream(body.get(AUTH).toString().split(","))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            User principal = new User(body.getSubject(), "", authority);
            Authentication authentication = new UsernamePasswordAuthenticationToken(principal, jwt, authority);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            return false;
        }
        return body != null;
    }

    public String isToken(HttpServletRequest request){
        String authorization = request.getHeader(TokenProvide.AUTHORIZATION_HEADER);
        if (StringUtils.isNotEmpty(authorization)){
            return authorization;
        }

        Cookie[] cookies = request.getCookies();
        if(ObjectUtils.isNotEmpty(cookies)){
            for (Cookie cookie: cookies){
                if (StringUtils.equals(cookie.getName(), WEB_COOKIE_NAME)){
                    return cookie.getValue();
                }
            }
        }
        return "";
    }
}
