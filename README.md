## 简介

欢迎使用 HTTP Sign.  
本项目将解决HTTP通信中的如下问题:

- 防止重放攻击
- 防止中途篡改数据
- 保证请求服务幂等

从而,尽可能地让HTTP通信接近安全.  

## 使用

```xml
<dependency>
    <groupId>org.fastquery</groupId>
    <artifactId>httpsign</artifactId>
    <version>1.0.2</version>
</dependency>
``` 

### 准备一个JAX-RS Resource Classes

```java
@javax.ws.rs.Path("helloworld")
public class HelloWorldResource {

  @org.fastquery.httpsign.Authorization // 作用在方法上,那么该方法将进行签名认证
  @javax.ws.rs.GET
  @javax.ws.rs.Produces("text/plain")
  public String getHello() {
      return "hi";
  }
  
}
```

### 编写作用于服务端的`ContainerRequestFilter` 

```java
@org.fastquery.httpsign.Authorization
public class AuthorizationContainerRequestFilter extends 
		org.fastquery.httpsign.AuthAbstractContainerRequestFilter {
	@Override
	public String getAccessKeySecret(String accessKeyId) {
		// 根据 accessKeyId 找出 accessKeySecret
	}
}
```

### 编写作用于客户端的`ClientRequestFilter`

```java
public class AuthorizationClientRequestFilter extends 
		org.fastquery.httpsign.AuthAbstractClientRequestFilter {
	@Override
	public String getAccessKeySecret(String accessKeyId) {
		// 根据 accessKeyId 找出 accessKeySecret
	}
}

```

### 在Jersey环境里使用

服务端:
 
```java
@ApplicationPath("userResorce")
public class Application extends ResourceConfig {
	public Application() throws IOException {
		register(AuthorizationContainerRequestFilter.class);
	}
}
```

JAX-RS客户端:

```java
javax.ws.rs.client.Client client = javax.ws.rs.client.ClientBuilder.newClient();
client.register(AuthorizationClientRequestFilter.class);
javax.ws.rs.client.WebTarget target = client.target("http://localhost:8080").path("userResorce/greet");
// ... ...
```

### 在CXF环境里使用

服务端:

```xml
<jaxrs:server address="http://localhost...">
	<jaxrs:serviceBeans>
		<bean class="... ..." /> 
	</jaxrs:serviceBeans>
	<jaxrs:providers>
		<bean class="org.fastquery.httpsign.sample.AuthorizationContainerRequestFilter" />
	</jaxrs:providers>
</jaxrs:server>
```

客户端:

```xml
<jaxrs:client address="<your request address>" serviceClass="<your request service>">
	<jaxrs:providers>
		<bean class="org.fastquery.httpsign.sample.AuthorizationClientRequestFilter" />
	</jaxrs:providers>
</jaxrs:client>
```

##  HTTP Sign 的设计

### 字面约定
|字面格式|含义|
|:-----|:-----|
|&lt; &gt;|变量|
|[ ]|可选项|
|{ }|必选项|
|&#124;|互斥关系|
|标点符号|本文一律采用英文标点符号|

### 请求参数名,命名规则
1. 首字母小写,如果名称由多个单词组成,每个单词的首字母要大写
2. 英文缩写词一律小写
3. 只能由 [A\~Z]、[a\~z]、[0\~9] 以及字符"-"、"_"、"." 组成参数名
4. 不能以数字开头
5. 不允许出现中文及拼音命名

### 术语表
| 术语 | 全称 | 中文 | 说明 |
|:-----|:-----|:-----|:-----|
|RS|RESTful Web Services|WEB REST服务|REST 架构风格的Web服务|
|SecurityGroup|Security Group|安全组|安全组制定安全策略|
|GMT|Greenwich Mean Time|格林尼治标准时间|指位于英国伦敦郊区的皇家格林尼治天文台的标准时间|
|URIPath|Uniform Resource Identifier Path|统一资源标识符的路径|用于标识某一互联网资源路径|
|RFC|Request For Comments|一系列以编号排定的文件|几乎所有的互联网标准都有收录在RFC文件之中|

### 相关名词解释
1. **字典升序排列**  
如同在字典中排列单词一样排序,按照字母表递增顺序排列,参与比较的两个单词,若它们的第一个字母相同,就比较第二个字母,依此类推.  
例如: zhong zhang zheng zhen, 做字典升序排列后的结果是 zhang zhen zheng zhong. 

2. **幂等性**  
接口在设计上可以被完全相同的URL重复调用多次,而最终得到的结果是一致的.

### 使用限制

请求端的当前时间与服务器的当前时间之差的绝对值不能大于10分钟,否则拒绝处理. 也就是说,请求端的时间不能比服务器时间快10分钟或慢10分钟,否则,服务器不受理.

## 请求结构
1. 服务地址  
接口按照功能划分成了不同的功能模块,每个模块使用不同的域名或上下文访问,具体域名或上下文请参考各个接口的文档.

2. 通信协议  
所有接口均采用HTTPS通信.

3. 请求方法  
支持 [GET,POST,PUT,DELETE,PATCH,HEAD,OPTIONS].

4. 字符编码  
在无特别说明情况下,均使用UTF-8编码.

5. API请求结构  

	|名称|描述|备注|
	|:-----|:-----|:-----|
	|API入口|API调用的RS服务的入口|`https://<domain>/path/hi`|
	|公共header|每个接口都包含的通用请求头|详见 [公共参数](#公共请求头common-request-headers)|
	|公共参数|每个接口都包含的通用参数|详见 [公共参数](#公共请求头common-request-headers)|


## 公共参数
### 公共请求头(Common Request Headers)
|名称|是否必选|描述|
|:-----|:-----:|:-----|
|Authorization|是|用于验证请求合法性的认证信息|
|Accept|是|默认:"application/json",表示发送端(客户端)希望从服务端接受到的数据类型|
|Content-Length|是|[RFC2616](https://tools.ietf.org/html/rfc2616)中定义的HTTP请求内容长度(一般的http客户端工具都会自动带上这个请求头)|
|Date|是|HTTP 1.1协议中规定的GMT时间,例如：Wed, 28 Mar 2018 09:09:19 GMT|
|Host|是|访问Host值(一般的http客户端工具都会自动带上这个请求头)|


### 公共请求参数(Common Http Request Parameters)
|名称|是否必选|类型|描述|
|:-----|:-----:|:-----:|:-----|
|version|是|`String`|API 版本号,当前值为1|
|action|是|`String`|接口的指令名称,如:action=myInfo|
|nonce|是|`String`|随机数,长度范围\[8,36\]|
|accessKeyId|是|`String`|在云API密钥上申请的标识身份的 accessKeyId,一个 accessKeyId 对应唯一的 accessKeySecret , 而 accessKeySecret 会用来生成请求签名 Signature|
|signatureMethod|否|`String`|签名算法,目前支持HMACSHA256和HMACSHA1.默认采用:HMACSHA1验证签名|
|token|否|`String`|临时证书所用的Token,需要结合临时密钥一起使用|

服务端将从 QueryString 获得这些参数. 


## 签名机制
用户在HTTP请求中增加`Authorization`的Header来包含签名(Signature)信息,表明这个消息已被签名,认证是否通过,服务端说了算.  
Authorization的值如何得到,其计算规则如下:

```java
Signature = base64(SignatureMethod(AccessKeySecret,
            HttpMethod + "\n"
            + Content-MD5 + "\n" //注意: 如果Content-MD5为""或null,后面就不能 + "\n" 了(去掉该行)
            + Accept + "\n" 
            + Date + "\n" 
            + BuildCustomHeaders + "\n" //注意: 如果BuildCustomHeaders为""或null,后面就不能 + "\n" 了(去掉该行)
            + URIPath + "\n"
            + BuildRequestParameters))
            
Authorization = "Basic " + Signature  
```

- 1.SignatureMethod  
可选算法,HMACSHA256和HMACSHA1.

- 2.AccessKeySecret  
服务端颁发给用户的密钥,不能泄露,只允许用户知道. 

- 3.HttpMethod 
可选值,GET,POST,PUT,DELETE,PATCH,HEAD,OPTIONS.

- 4.Content-MD5  
表示请求主体(Request Body)数据的MD5值,对消息内容(不包括头部)计算MD5值获得128bit(比特位)数字,对该数字进行Base64编码而得到,如果没有Body该值为""(空字符串).  
注意: Content-MD5如果为""(空字符串),末尾的"\n"必须去掉.     
假设,body内容为"**好好学习,天天向上**",计算其Content-MD5,以Java代码作为示例:
	
	```java
	// 待计算的内容
	String content = "好好学习,天天向上";
	byte[] input = content.getBytes(java.nio.charset.Charset.forName("utf-8"));
	
	// 1. 先计算出MD5加密的字节数组(16个字节)
	java.security.MessageDigest messageDigest = java.security.MessageDigest.getInstance("MD5");
	messageDigest.update(input);
	byte[] md5Bytes =messageDigest.digest();
	
	// 2. 再对这个字节数组进行Base64编码(而不是对长度为32的MD5字符串进行编码)。
	// Java 8+ 中自带的Base64工具(java.util.Base64)
	String str = java.util.Base64.getEncoder().encodeToString(md5Bytes);
	
	// 正确的值应该是 "BheE8OSZqgEXBcg6TjcrfQ=="
	
	// 断言
	assertThat(str, equalTo("BheE8OSZqgEXBcg6TjcrfQ=="));
	```

- 5.Accept   
可选值: application/json 或 application/xml.

- 6.Date  
表示此次请求的当前时间,必须为GMT时间,如"Wed, 28 Mar 2018 09:09:19 GMT".  
以Java代码作为示例,怎么获得GMT时间:

```java
// RFC 822 日期格式
String f = "EEE, dd MMM yyyy HH:mm:ss z";
java.text.SimpleDateFormat rfc822DateFormat = new java.text.SimpleDateFormat(f, java.util.Locale.US);
rfc822DateFormat.setTimeZone(new java.util.SimpleTimeZone(0, "GMT"));

// 将date格式化成GMT时间格式的字符串
java.util.Date date = new java.util.Date();
String gmtStr = rfc822DateFormat.format(date);

// 将GMT时间格式的字符串解析成Date对象
java.util.Date d = rfc822DateFormat.parse(gmtStr);
```

- 7.BuildCustomHeaders  
	所有以`X-Custom-`做为前缀的HTTP Header被称为自定义请求头.    
	BuildCustomHeaders构建规则如下:  
	7.1 将所有以`X-Custom-`为前缀的HTTP请求头的名字转换成小写,例如将"X-Custom-Meta-Author: FastQuery"转换成"x-custom-meta-author: FastQuery".    
	7.2 将上一步得到的所有HTTP请求头做字典升序排列.  
	7.3 请求头名称与内容之间用":"号隔开,并且需要清空分割符":"左右的空白.例如需要将"x-custom-meta-author : FastQuery"处理成"x-custom-meta-author:FastQuery".  
	7.4 每个完整的请求头(头名称:内容),它们之间用"\n"进行分隔,最后拼接成BuildCustomHeaders.  
	7.5 BuildCustomHeaders 允许为""(空字符串).  
	举例:  	
	若有一个请求头"X-CUSTOM-META-A:xx",那么,BuildCustomHeaders为"x-custom-meta-a:xx".  
	若有2个请求头"X-CUSTOM-META-A:xx","X-CUSTOM-META-B:yy",那么,BuildCustomHeaders为"x-custom-meta-a:xx\nx-custom-meta-b:yy".  

- 8.URIPath   
URL端口与QueryString之间的地址,不含"?",在此称之为URIPath.举例:   
若有请求URL"`https://<domain><默认80可以省略>/path/hi?action=myInfo`",那么URIPath为"/path/hi".  
若有请求URL"`https://<domain>:8080/path/hi?action=myInfo`",那么URIPath为"/path/hi".  
若有请求URL"`https://<domain>:8080/path/hi`",那么URIPath为"/path/hi".  
若有请求URL"`https://<domain>:8080/`",那么URIPath为"/".  
若有请求URL"`https://<domain>:8080?action=myInfo`",那么URIPath为"".  
以Java代码为示例,获取URIPath:

	```java
	public class AuthorizationClientRequestFilter implements javax.ws.rs.client.ClientRequestFilter {
		@Override
		public void filter(javax.ws.rs.client.ClientRequestContext requestContext) {
			java.net.URI uri = requestContext.getUri();
			String uriPath = uri.getPath();
			LOG.debug("uriPath:{}",uriPath);
		}
	}
	```
  
- 9.BuildRequestParameters,构建规则如下:

	- 9.1. 对参数排序  
	对所有请求参数按参数名做字典升序排列.  
	实际上就是按照ASCII码从小至大排序,举例:
	
		|字母|ASCII码对应的10进制|
		|:-----:|:-----:|
		|A|65|
		|N|78|
		|R|82|
		|S|83|
		|T|84|
		|i|105|
		|l|108|
		|o|111|
	
	则,做字典升序排列后的顺序是:A N R S T i l o  
 
	- 9.2. 对参数编码  
		对做字典升序排列之后的请求参数的值进行URL编码(参数名称严格按照上文提及到的命名规范,因此不用编码,因为它的组成字符都是[RFC3986](https://tools.ietf.org/html/rfc3986?spm=a2c4g.11186623.2.6.qtLqZF)中明确说明的不用编码的字符).遵循[RFC3986](https://tools.ietf.org/html/rfc3986?spm=a2c4g.11186623.2.6.qtLqZF)规定,编码规则如下:  
		
		- 9.2.1. 参数值用UTF-8字符集; 

		- 9.2.2. 对于字符 A\~Z、a\~z、0\~9 以及字符"-"、"_"、"."、"\~"不编码;  

		- 9.2.3. 对其它字节做[RFC3986](https://tools.ietf.org/html/rfc3986?spm=a2c4g.11186623.2.6.qtLqZF)中规定的百分号编码(Percent-encoding),即一个"%"后面跟着两个表示该字节值的十六进制字母,字母一律采用大写形式.其格式:%XY,其中 XY 是字符对应 ASCII 码的 16 进制表示.  
		比如:  
		英文的空格" ",采用UTF-8字符集,对应的字节是:0X22, 因此其URL编码为%22;    
		英文字符的"*",采用UTF-8字符集,对应的字节是:0X2A, 因此其URL编码为%2A.  

		- 9.2.4. 对于扩展的 UTF-8 字符,编码成 %AB%CD 的格式；  
		最初十进制[0,127],共128个代码是ASCII. 然而,大于127以上ASCII后面跟着第二个字节.这两个字节一起定义一个字符.   
		举例:

			|字符|采用UTF-8字符集对应的字节|
			|:-----:|:-----:|
			|α|0XCEB1|
			|β|0XCEB2|
			|γ|0XCEB3|
			
			那么,将可以算出URL
			
			|字符|URL代码|
			|:-----:|:-----:|
			|α|%CE%B1|
			|β|%CE%B2|
			|γ|%CE%B3|
  

		- 9.2.5. 使用编码工具应该注意的事项    
		该编码方式和一般采用的 application/x-www-form-urlencoded MIME 格式编码算法相似,但又有所不同.  
		比如 Java 标准库中的 java.net.URLEncoder 实现了application/x-www-form-urlencoded MIME 格式编码, 就拿它来做比喻.  
		`URLEncoder.encode("~", "utf-8")` 输出的结果是 `%7E`, [RFC3986](https://tools.ietf.org/html/rfc3986?spm=a2c4g.11186623.2.6.qtLqZF)规定中不对`~`进行编码.  
		`URLEncoder.encode("*", "utf-8")` 输出的结果是 `*`, [RFC3986](https://tools.ietf.org/html/rfc3986?spm=a2c4g.11186623.2.6.qtLqZF)规定,没有说不对`*`这个符号进行编码.   
		`URLEncoder.encode(" ", "utf-8")` 输出的结果是 `+`,   [RFC3986](https://tools.ietf.org/html/rfc3986?spm=a2c4g.11186623.2.6.qtLqZF)规定,编码结果采用%XY格式(XY: 16进制字面).  
		目前发现这些差异性   
		因此,使用JAVA的URLEncoder进行URL编码,不能满足我们所约定的编码规范,需要对它的处理结果稍作该进.   
		将URLEncoder.encode处理的结果的`+` 替换成`%20`,`*` 替换为 `%2A` `%7E` 替换回`~`.  
		
			```java
			private static String specialUrlEncode(String value) throws UnsupportedEncodingException {
				return URLEncoder.encode(<待编码字符串>, "utf-8").replace("+", "%20").replace("*", "%2A")
				.replace("%7E", "~");
			}
			```

	- 9.3. 拼接参数   
	按字典升序排列后,参数值经过上个步骤编好码后, 参数名和参数值用`=`连接,参数与参数之间用`&`连接. 截至这里,BuildRequestParameters构建完成.  
	
	- 9.4 举例:  
	假设有6个参数:   
	
		```js
		{
		    "nonce" : "1aabcde-5268-3326-c845-56kljgwexe",
		    "action" : "myInfo",
		    "offset" : 1,
		    "secretKeyId" : "BKJGW40598092JXMWNRF",
		    "limit" : 15
		}
		```
		
		步骤1: 对参数做字典升序排列  
	
		```js
		{
		    "action" : "myInfo",
		    "limit" : 15,
		    "nonce" : "1aabcde-5268-3326-c845-56kljgwexe",
		    "offset" : 1,
		    "secretKeyId" : "BKJGW40598092JXMWNRF"
		}
		```
	
		步骤2: 遵循[RFC3986](https://tools.ietf.org/html/rfc3986?spm=a2c4g.11186623.2.6.qtLqZF)对请求参数的值进行URL编码  
	
		步骤3: 拼接参数  
		**action=myInfo&limit=15&nonce=1aabcde-5268-3326-c845-56kljgwexe&offset=1&secretKeyId=BKJGW40598092JXMWNRF** 这就是BuildRequestParameters. 
		
**例**,根据如下假设,计算出`Authorization`.  
设, AccessKeySecret 为: "KYA8A4-74E17B58B093";  
设, 签名算法为:"HMACSHA1";  
设, URIPath为:"/httpsign/userResorce/greet"  
设,请求方法(Request Method)为: `POST`;  
设,请求头为:

|请求头名称|值|
|:-----|:-----|
|Authorization|待计算|
|Accept|"application/json"|
|Date|"Wed, 11 Apr 2018 06:03:43 GMT"|
|X-Custom-Meta-Author|"FastQuery.HttpSign"|
|X-Custom-Meta-Description|"HTTP authentication techniques."|
|X-Custom-Meta-Range|"52363"|
  
设,请求参数(Request Parameters)为:

|参数名称|值|
|:-----|:-----|
|accessKeyId|"AP084671DF-5F8C-41D2"|
|typeId|7|
|nonce|"e6e03b6f-7de2-4d02-8e04-3ccbad143389"|

设,请求Body为:"**蚓无爪牙之利，筋骨之强，上食埃土，下饮黄泉，用心一也**".   

**解**:  
此解,意在阐述计算Authorization的过程,为了便于读者阅读,故,代码紧凑一看到底.

```java
// 密钥
String accessKeySecret = "KYA8A4-74E17B58B093";

String uriPath = "/httpsign/userResorce/greet";
String httpMethod = "POST";
String accept = "application/json";
String date = "Wed, 11 Apr 2018 06:03:43 GMT";

// 构建请求头
java.util.TreeMap<String, String> headerTreeMap = new java.util.TreeMap<>();
headerTreeMap.put("X-Custom-Content-Range", "52363");
headerTreeMap.put("X-Custom-Meta-Author", "FastQuery.HttpSign");
headerTreeMap.put("X-Custom-Meta-Description", "HTTP authentication techniques.");
StringBuilder headersBuilder = new StringBuilder();
headerTreeMap.forEach((k, v) -> headersBuilder.append(k.toLowerCase()).append(':').append(v).append('\n'));
String headersStr = headersBuilder.toString();

// 构建请求参数
java.util.TreeMap<String, String> queryStringTreeMap = new java.util.TreeMap<>();
queryStringTreeMap.put("accessKeyId", "AP084671DF-5F8C-41D2");
queryStringTreeMap.put("typeId", "7");
queryStringTreeMap.put("nonce", "e6e03b6f-7de2-4d02-8e04-3ccbad143389");
StringBuilder requestParametersBuilder = new StringBuilder();
queryStringTreeMap.forEach((k, v) -> {
	try {
		requestParametersBuilder.append('&').append(k).append('=')
				.append(java.net.URLEncoder.encode(v, "utf-8").replace("+", "%20")
				.replace("*", "%2A").replace("%7E", "~"));
	} catch (java.io.UnsupportedEncodingException e) {
		throw new RuntimeException("URL编码出错", e);
	}
});
String requestParameters = requestParametersBuilder.substring(1);

// 计算Content-MD5的值
String requestBody = "蚓无爪牙之利，筋骨之强，上食埃土，下饮黄泉，用心一也";
byte[] input = requestBody.getBytes(java.nio.charset.Charset.forName("utf-8"));
java.security.MessageDigest messageDigest = java.security.MessageDigest.getInstance("MD5");
messageDigest.update(input);
byte[] md5Bytes = messageDigest.digest();
String contentMD5 = java.util.Base64.getEncoder().encodeToString(md5Bytes);

// 构建 stringToSign
StringBuilder sb = new StringBuilder();
sb.append(httpMethod).append('\n');
sb.append(contentMD5).append('\n');
sb.append(accept).append('\n');
sb.append(date).append('\n');
sb.append(headersStr);
sb.append(uriPath).append('\n');
sb.append(requestParameters);
String stringToSign = sb.toString();

// 计算出signature
javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HMACSHA1");
mac.init(new javax.crypto.spec.SecretKeySpec(accessKeySecret.getBytes(
		java.nio.charset.Charset.forName("utf-8")), "HMACSHA1"));
byte[] signData = mac.doFinal(stringToSign.getBytes(java.nio.charset.Charset.forName("utf-8")));
String signature = java.util.Base64.getEncoder().encodeToString(signData);

// 得出authorization
String authorization = "Basic " + signature;
// 断言:authorization等于"Basic 3qo3tKAYM16Pr88Lpr5WPj2VJco="
org.junit.Assert.assertThat(authorization, 
		org.hamcrest.Matchers.equalTo("Basic 3qo3tKAYM16Pr88Lpr5WPj2VJco="));
```

截至这里, 解毕.

## 返回结果
### 正确返回结果
若 API 调用成功,错误码`code`为0,并且会返回结果数据.  
示例如下:

```js
{
	code:0,
	data:<结果数据>
}
```

### 错误返回结果
若 API 调用失败,错误码`code`不为 0,`message`字段会显示详细错误信息(成功返回没有该字段).  
示例如下:

```js
{
    "code": 40001,
    "message": "传递的请求头Authorization不符合规范."
}
```

#### 标准公共错误码
根据[RFC 2616](https://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html)定义,将如下状态码定义(Status Code Definitions)作为公共错误码:

|错误码|描述|
|:-----:|:-----|
|400|Bad Request|
|401|Unauthorized|
|402|Payment Required|
|403|Forbidden|
|404|Not Found|
|405|Method Not Allowed|
|406|Not Acceptable|
|407|Proxy Authentication Required|
|408|Request Timeout|
|409|Conflict|
|410|Gone|
|411|Length Required|
|412|Precondition Failed|
|413|Request Entity Too Large|
|414|Request-URI Too Long|
|415|Unsupported Media Type|
|416|Requested Range Not Satisfiable|
|417|Expectation Failed|
|428|Precondition Required|
|429|Too Many Requests|
|431|Request Header Fields Too Large|
|500|Internal Server Error|
|501|Not Implemented|
|502|Bad Gateway|
|503|Service Unavailable|
|504|Gateway Timeout|
|505|HTTP Version Not Supported|
|511|Network Authentication Required|


#### 自定义公共错误码
自定义错误码由5位数字组成(除0表示成功外),前3位数表示对应的HTTP状态码(HTTP Status Code).目前自定义的错误前缀如下:

- 400XX 请求错误
- 403XX 被禁止的
- 404XX 找不到
- 500XX 内部错误
- 503XX 服务不可用

|错误码|描述|
|:-----:|:-----|
|40000|没有传递请求头Authorization.|
|40001|传递的请求头Authorization不符合规范.|
|40002|传递的请求头Accept不符合要求,要么是"application/json" 要么是 "application/xml".|
|40003|请求头Date必须传递,并且必须是HTTP 1.1协议中规定的GMT时间.|
|40004|请求端的时间不能比服务器时间快10分钟或慢10分钟.|
|40005|没有传递请求参数version.|
|40006|传递的version参数,不符合要求.|
|40007|名称为action的请求参数没有传递.|
|40008|名称为nonce的请求参数没有传递.|
|40009|nonce的长度不能超过36且不能小与8.|
|40010|名称为accessKeyId的请求参数没有传递.|
|40011|根据accessKeyId没有找到对应的accessKeySecret.|
|40012|签名算法要么传递HMACSHA1或HMACSHA256,要不传递(默认:HMACSHA1).|
|40013|传递的token错误.|
|40014|token认证失败.|
|40015|有请求body,而没有传递请求头Content-MD5.|
|40016|计算请求body的MD5出错.|
|40017|计算Authorization出错.|
|40018|传过来的Authorization是错的.|
|40300|在10分钟内不能传递相同的随机码.|
|50300|服务不可用.|

版权归[习习风](https://gitee.com/xixifeng.com)所有,请认准开源地址:   
https://gitee.com/xixifeng.com/httpsign  
https://github.com/xixifeng/httpsign   
以获得最近更新.

## 推荐开源项目
- [https://gitee.com/xixifeng.com/fastquery](https://gitee.com/xixifeng.com/fastquery) 基于ASM的DB操作框架
- [https://gitee.com/xixifeng.com/pjaxpage](https://gitee.com/xixifeng.com/pjaxpage) 支持PJAX的分页解决方案
- [https://gitee.com/xixifeng.com/httpsign](https://gitee.com/xixifeng.com/httpsign) RESTful API 签名认证 

