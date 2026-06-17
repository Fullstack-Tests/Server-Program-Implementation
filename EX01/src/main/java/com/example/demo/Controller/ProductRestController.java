package com.example.demo.Controller;

import com.example.demo.Domain.Common.Dtos.ProductDTO;
import com.example.demo.Domain.Common.Service.ProductService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/products")
@CrossOrigin(originPatterns = {"http://localhost:[*]"})
public class ProductRestController {

    @Autowired
    private ProductService productService;

    // TODO: 목록 조회
    //  - productService.list(pageable) 호출
    //  - 결과의 content / number / totalPages / totalElements 를 Map 에 담아
    //    ResponseEntity 로 200(OK) 반환
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> list(Pageable pageable) {
        log.info("GET /api/products..." + pageable);    //요청 로그
        Map<String, Object> responseMap = new HashMap<>();  //응답 데이터를 담을 Map 생성

        Page<ProductDTO> r = productService.list(pageable); //페이징 처리된 상품 목록 조회

        responseMap.put("content", r.getContent()); //현재 페이지의 데이터 목록
        responseMap.put("number", r.getNumber());   //현재 페이지 번호 (0부터 시작)
        responseMap.put("totalPages", r.getTotalPages());   //전체 페이지 수
        responseMap.put("totalElements", r.getTotalElements()); //전체 데이터 건수

        return ResponseEntity.ok(responseMap);  //200 OK + 응답 데이터 반환
    }


    // TODO: 단건 조회
    //  - productService.get(id) 결과를 200(OK) 으로 반환
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductDTO> get(@PathVariable("id") Long id) {

        return ResponseEntity.ok(productService.get(id));   //id로 단건 조회 후 결과를 바로 200 OK로 반환
    }



    // TODO: 등록
    //  - @RequestBody @Valid ProductDTO + BindingResult 로 검증
    //  - bindingResult.hasErrors() 이면 FieldError 들을 돌며 필드별 메시지를 Map 에 담아 400(BAD_REQUEST) 반환
    //  - 통과 시 productService.register(dto) 호출 후 200 + "상품 등록 성공!" 메시지 반환
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> add(@RequestBody @Valid ProductDTO dto,
                                                   BindingResult bindingResult) {
        return null;
    }


    // TODO: 수정
    //  - @PathVariable id + @RequestBody @Valid ProductDTO + BindingResult
    //  - 검증 오류 처리는 등록과 동일 / 통과 시 productService.modify(id, dto) → 200 + "상품 수정 성공!"
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> update(@PathVariable("id") Long id,
                                                       @RequestBody @Valid ProductDTO dto,
                                                       BindingResult bindingResult) {
        return null;
    }

    // TODO: 삭제
    //  - productService.remove(id) 호출 후 200 + "상품 삭제 성공!" 메시지 반환
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable("id") Long id) {
        return null;
    }

    // TODO: 일괄 등록 (한 트랜잭션, 중간 실패 시 전체 롤백)
    //  - @RequestBody List<ProductDTO>
    //  - productService.registerBulk(list) 호출 후 등록 건수(count) + "일괄 등록 성공!" 반환
    @PostMapping(value = "/bulk", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> addBulk(@RequestBody List<ProductDTO> list) {

        return null;
    }

}
