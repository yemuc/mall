package com.yemu.mall.common;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.util.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yemu
 */
public class TokenUtil {
    /**
     * token的生成对的验证
     */
    //token密钥 cspmall
    public static final String SECRET = "cspmall";
    //过期时间
    public static final int CALENDAR_INTERVAL = 7;
    public static final int CALENDAR_FIELD = Calendar.DATE;

    /**
     *生成token
     * @param uid 登录成功后token带用户id
     * @return Token
     */
    public static String createToken(Integer uid){
        //发布时间
        Date iatDate = new Date();
        //设置过期时间
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(CALENDAR_FIELD, CALENDAR_INTERVAL);
        //过期时间
        Date expireDate = nowTime.getTime();

        // header Map
        Map<String, Object> map = new HashMap<>();
        map.put("alg","HS256");
        map.put("typ","JWT");

        //创建token
        return JWT.create().withHeader(map)//header
                .withClaim("iss","Service")//发行人
                .withClaim("aud","App")//用户
                .withClaim("uid",null == uid ? null : uid.toString())//信息
                .withIssuedAt(iatDate)//发布时间
                .withExpiresAt(expireDate)//过期时间
                .sign(Algorithm.HMAC256(SECRET));
    }

    /**
     * 解码token
     * @param token 客户端传入的token
     * @return 解码后的token
     */
    public static Map<String, Claim> decodeToken(String token){
        DecodedJWT jwt;
        try{
            if (token!=null){
                JWTVerifier verifier=JWT.require(Algorithm.HMAC256(SECRET)).build();
                jwt = verifier.verify(token);
                return jwt.getClaims();
            }
            else {
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 验证token是否有效
     * @param token token
     * @return boolean
     */
    public static boolean verifyToken(String token){
        try {
            if (null==token||token.isEmpty()){
                return false;
            }
            Date nowTime = new Date();
            Date expTime = JWT.decode(token).getExpiresAt();
            // 验证是否过期
            if (nowTime.after(expTime)){
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static int getUID(String token) {
        try {
            // 验证有效性
            if (!verifyToken(token)){
                return 0;
            }
            //解码token
            Map<String, Claim> claimMap = decodeToken(token);

            if (null == claimMap) {
                return 0;
            }
            //从解码后的token获取uid信息
            Claim uidClaim = claimMap.get("uid");
            if (null == uidClaim || StringUtils.isEmpty(uidClaim.asString())) {
                return 0;
            }
            return Integer.parseInt(uidClaim.asString());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }
}
