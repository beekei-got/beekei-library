# Beekei library
제가 자주 사용하는 코드를 라이브러리로 제작하였습니다.
계속해서 라이브러리를 추가할 예정이고 오류가 발견될 경우 업데이트 예정입니다.

## QuerydslBuilder library
- Querydsl을 Builder 패턴으로 사용하기 쉽게 도와주는 라이브러리입니다.
### 설정방법
```
@PersistenceContext
private EntityManager entityManager;
	
@Bean
public QuerydslBuilder querydslBuilder() {
    return new QuerydslBuilder(JPQLTemplates.DEFAULT, entityManager);
}
```
### 사용법
```
@Autowired
private QuerydslBuilder querydslBuilder;

querydslBuilder
    .select(Projections.constructor(UserInfoDTO.class))
    .from(user) # user의 Qclass
    .where(user.id.eq(1L))
    .getRow();
```
```
# Projections.constructor로 조회할 DTO
@Getter
@AllArgsConstructor 
@NoArgsConstructor # 기본 생성자 필수 
public class UserInfoDTO implements QuerydslSelectDTO {

    private long userId;
    private String userName;
    
    # QuerydslSelectDTO의 constructor 메서드를 구현
    @Override
    public ConstructorExpression<?> constructor() {
        return Projections.constructor(ClientUserInfoDTO.class,
            user.id,
            user.userStatus,
            user.userName,
            user.nickname,
            user.email,
            user.gender,
            user.birthday,
            user.phoneNumber);
	}
}
```

## ApiVersion library
- API에 version을 지정할 수 있는 라이브러리입니다.
### 설정방법
```
@Bean
public WebMvcRegistrations webMvcRegistrations() {
    return new WebMvcRegistrations() {
        @Override
        public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
            return new ApiVersionRequestMappingHandlerMapping("api/v");
        }
    };
}
```
### 사용법
```
@ApiVersion(1)
@RequestMapping(path = "user") 
public class UserController { 
    # requestPath : api/v1/user
}
```

## MockTest library
- Test를 더욱 편리하게 진행하기 위한 라이브러리입니다.
### 사용법
```
# Domain Service Test
class UserDomainServiceTest extends DomainServiceTest {

    @MockBean
    @InjectMocks
    private UserDomainService userDomainService;

    @Mock
    private UserRepository userRepository;
    
    ...   
}

# Application(Business) Service Test
class UserJoinService extends ApplicationServiceTest {

    @MockBean
    @InjectMocks
    private UserJoinService userJoinService;

    @Mock
    private UserDomainService userDomainService;
        
    ...
}   

# Controller Test
class UserJoinControllerTest extends ControllerTest {
    
    protected UserJoinControllerTest() {
        # 해당 Controller에 설정된 RequestMapping, ApiVersion 정보가 기본 API Path로 설정됩니다.
        super(UserJoinController.class);
    }
	
    @MockBean
    private UserJoinService userJoinService;
    
    @Test
    @DisplayName("회원 가입")
    void joinUser() {
        ...
        
        # ex)
        # UserJoinController 
        # RequestMapping(path="user") / @ApiVersion(1) 
        mvcTest(HttpMethod.POST, "join") # POST api/v1/user/join
            .requestBody(Map.entry("userName", "홍길동"))
            .responseHttpStatus(HttpStatus.OK)
            .responseBody(
                Map.entry("resultCode", "200"),
                Map.entry("resultMessage", "Api call Success!")
            .run();
    }
    
}
```
