- test 폴더에서는 test/resources가 우선권이 있기 때문에 테스트 코드 실행 시 test/resources/*.yml이 존재한다면 먼저 실행된다.
- 외부 디비 연결 없이 메모리에서만 테스트 하려 h2 database 설정 시 url을 다음과 같이 입력해주면 된다. "url: jdbc:h2:mem:test"
- 그런데 스프링부터에서는 기본적이 설정이 없으면 메모리 모드로 돌리기 때문에 database 연결 정보를 지워도된다.
   
### Cascade
- 주인이 하나인 경우에만 사용하는것을 권장
- 다른곳에서도 참조하게 된다면 cascade를 남용하면 안된다.
- 즉, 라이프사이클이 같다면 사용을 권장

### 빈 주입
#### 1. @Autowired
```java
@Autowired
private MemberRepository memberRepository; 
```
- 단점으로는 테스트 시 주입되는 값을 변경해야 될 때가 있는데 변경할 수 없다.

#### 2. setter injection
```java
private MemberRepository memberRepository;

@Autowired
public void setMemberRepository(MemberRepository memberRepository) {
    this.memberRepository = memberRepository;    
}
```
- 테스트 시 가짜 Repository를 넘길 수 있다.
- 단점으로는 런타임 시(애플리케이션이 돌고있는 시점)에 누군가 값을 변경할 수 있다.

#### 3. 생성자 injection
```java
private MemberRepository memberRepository;

@Autowired
public MemberService(MemberRepository memberRepository) {
    this.memberRepository = memberRepository;    
}
```
- 제일 권한하는 방법
- 스프링이 뜰 때 생성자로 주입 해준다.
- 다음과 같이 생성자가 하나 밖에 없을 때는 스프링이 자동으로 주입을 해준다.
```java
public MemberService(MemberRepository memberRepository) {
    this.memberRepository = memberRepository;    
}
```
- 주입되는 필드에 대해서는 final로 선언할 것을 권장(생성자만 만들어두고 초기화를 안하는 경우 컴파일 에러로 알려줌)
- 다음과 같이 롬복을 사용할 수 있다.
```java
@Service
@RequiredArgsContructor
public class MemberService {
    private final MemberRepository memberRepository;
}
```
- 주입되는 필드 이외의 필드가 필요할 수 있기 때문에 AllArgsConstructor보다는 RequiredArgsConstructor를 사용할 것을 권장


### JPA에서 DTO로 바로 조회
- 엔티티를 DTO로 변환하거나, DTO로 바로 조회하는 두 가지 방법은 각각 장단점이 있다. 둘중 상황에 따라서 더 나은 방법을 선택하면 된다. 엔티티로 조회하면 리포지토리 재사용성도 좋고, 개발도 단순해진다. 따라서 권장하는 방법은 다음과 같다.

#### 쿼리 방식 선택 권장 순서
1. 우선 엔티티를 DTO로 변환하는 방법을 선택한다.
2. 필요하면 페치 조인으로 성능을 최적화 한다. -> 대부분의 성능 이슈가 해결된다.
3. 그래도 안되면 DTO로 직접 조회하는 방법을 사용한다.
4. 최후의 방법은 JPA가 제공하는 네이티브 SQL이나 스프링 JDBC Template을 사용해서 SQL을 직접 사용한다.


### 페치 조인 최적화
```java
public List<Order> findAllWithItem() {
return em.createQuery(
        "select distinct o from Order o " + 
                " join fetch o.member m" +
                " join fetch o.delivery d" +
                " join fetch o.orderItems oi" +
                " join fetch oi.item i", Order.class)
        .getResultList();
}
```
- 페치 조인으로 SQL이 1번만 실행됨
- distinct를 사용한 이유는 1대다 조인이 있으므로 데이터베이스 row가 증가한다. 그 결과 같은 order엔티티의 조회수도 증가히게 된다. JPA의 distinct는 SQL에 distinct를 추가하고, 더해서 같은 엔티티가 조회되면, 애플리케이션에서 중복을 걸러준다. 이 예에서 order가 컬렉션 페이 조인 때문에 중복 조회 되는 것을 막아준다.
- 단점
  - 페이징 불가능
- 참고: 컬렉션 페치 조인을 사용하면 페이징이 불가능하다. 하이버네이트는 경로 로그를 남기면서 모든 데이터를 DB에서 읽어오고, 메모리에서 페이징 해버린다(매우 위험하다).
- 참고: 컬렉션 페치 조인은 1개만 사용할 수 있다. 컬렉션 둘 이상에 페치 조인을 사용하면 안된다. 데이터가 부정합하게 조회될 수 있다.

#### 한계 돌파
- 먼저 ToOne(OneToOne, ManyToOne) 관계를 모두 페치조인 한다. ToOne 관계는 row수를 증가시키지 않으므로 페이징 쿼리에 영향을 주지 않는다.
- 컬렉션은 지연 로딩으로 조회한다.
- 지연 로딩 성능 최적화를 위해 hibernate.default_batch_fetch_size, @BatchSize를 적용한다.
  - hibernate.default_batch_fetch_size: 글로벌 설정
  - @BatchSize: 개별 최적화
  - 이 옵션을 사용하면 컬렉션이나, 프록시 객체를 한꺼번에 설정한 size만큼 IN 쿼리로 조회한다.
  ```yaml
  spring:
    jpa:
      properties:
        hibernate:
          default_batch_fetch_size: 1000    
  ```
- 개별로 설정하려면 @BatchSize를 적용하면 된다.(컬렉션은 컬렉션 필드에, 엔티티는 엔티티 클래스에 적용)
- 장점
  - 쿼리 호출 수가 1+N -> 1+1로 최적화 된다.
  - 조인보다 DB 데이터 전송량이 최적화 된다. (Order와 OrderItem을 조인하면 Order가 OrderItem 만큼 중복해서 조회된다. 이 방법은 각각 조회하므로 전송해야할 중복 데이터가 없다.)
  - 페치 조인 방식과 비교해서 쿼리 호출 수가 약간 증가하지만, DB 데이터 전송량이 감소한다.
  - 컬렉션 페치 조인은 페이징이 불가능 하지만 이 방법은 페이징이 가능하다.
> 참고: 'default_batch_fetch_size'의 크기는 적당한 사이즈를 골라야 하는데, 100~1000 사이를 선택하는 것을 권장한다.
> 이 전략은 SQL IN 절을 사용하는데, 데이터베이스에 따라 IN절 파라미터를 1000으로 제한하기도 한다.
> 1000으로 잡으면 한번에 1000개를 DB에서 애플리케이션에 불러오므로 DB에 순간 부하가 증가 할 수 있다.
> 하지만 애플리케이션은 100이든 1000이든 결국 전체 데이터를 로딩해야 하므로 메모리 사용량이 같다. 
> 1000으로 설정하는 것이 성능상 가장 좋지만, 결국 DB든 애플리케이션이든 순간 부하를 어디까지 견딜 수 있는지로 결정하면 된다.