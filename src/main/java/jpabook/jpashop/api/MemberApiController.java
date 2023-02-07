package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.Memberservice;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

//@Controller @ResponseBody 두개를 합쳐서 RestController로 적음
// 데이터 자체를 json이나 api로 바로 보낼때 @ResponseBody를 사용함
@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final Memberservice memberservice;

    //등록이므로 postMapping
    @PostMapping("/api/v1/members")     // 회원을 등록하는 api
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){
        //RequestBody -> Json으로 온 Body를 member에 매핑해준다.
        //  json 데이터를 멤버로 바꿔준다.
        Long id = memberservice.join(member);
        return new CreateMemberResponse(id);
        // 여기서 문제는 어떠한 방식으로 회원가입할지 알 수 없으니 엔티티를 파라미터로 넣으면 안된다. dto를 사용해야하는 이유.
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request){
        Member member = new Member();
        member.setName(request.name);

        Long id = memberservice.join(member);
        return new CreateMemberResponse(id);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request){

        //수정할때는 변경감지 사용하기
        memberservice.update(id, request.getName());
//        /query 와 command를 분리하여 개발한다. 개발한다 이렇게 할경우 추후에 유지보수가 비교적 쉬워진다.
        Member findMember = memberservice.findOne(id);

        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
        //response dto 반환
    }

    @Data
    static class UpdateMemberRequest{
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse{
        private Long id;
        private String name;
    }

    @Data
    static class CreateMemberRequest {
        @NotEmpty   // 이것이 가능 V1과는 다르게
        private String name;

    }
    @Data       // 응답값
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }
}
