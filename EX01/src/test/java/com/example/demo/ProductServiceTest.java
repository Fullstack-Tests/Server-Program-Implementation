package com.example.demo;

import com.example.demo.Domain.Common.Dtos.ProductDTO;
import com.example.demo.Domain.Common.Repository.ProductRepository;
import com.example.demo.Domain.Common.Service.ProductService;
import com.example.demo.Exception.MyBizException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional   // 테스트 후 자동 롤백
class ProductServiceTest {

    @Autowired private ProductService productService;
    @Autowired private ProductRepository productRepository;

    // TODO: 정상 등록 테스트
    //  - ProductDTO 빌더로 유효한 상품 생성 → register → id 가 생성되었는지 assertNotNull

    // 정상 등록 테스트
    @Test
    void register_정상() {

        ProductDTO dto = ProductDTO.builder()    // 등록할 상품의 DTO 생성
                .name("케이크")
                .price(50000)
                .stock(6)
                .category("제과")
                .build();

        ProductDTO result = productService.register(dto);  // DB에 상품 등록

        assertNotNull(result.getId());          // DB 저장 시 id 자동 생성되므로 null 아닌지 확인
    }

    // TODO: 일괄등록 중간 실패 시 전체 롤백 테스트
    //  - 먼저 상품 1건 등록(중복 유발용 이름)
    //  - bulk 리스트 2건 중 2번째를 위 이름과 중복으로 구성
    //  - registerBulk 호출 시 MyBizException 발생(assertThrows)
    //  - 전체 count 가 (등록 1건) 그대로인지(=bulk 0건 저장) 확인

    // 일괄등록 중간 실패 시 전체 롤백 테스트
    @Test
    void registerBulk_중간실패_전체롤백() {

        ProductDTO dto = ProductDTO.builder().name("바게트").price(6000).stock(10).category("제빵").build();  // 중복 유발 상품 1건 DTO 생성
        productService.register(dto);       // DB에 상품 등록 (count = 1)

        List<ProductDTO> dtoList = List.of(         // 등록할 상품 DTO list로 생성
                ProductDTO.builder().name("롤빵").price(15000).stock(7).category("제과").build(),       // 정상 상품 생성
                ProductDTO.builder().name("바게트").price(12000).stock(1).category("제빵").build()       // 이름 중복 상품 DTO 작성, 예외 발생
        );

        assertThrows(MyBizException.class, () -> productService.registerBulk(dtoList));     // 2번째 상품 이름 중복 예외 발생 확인, 트랜잭션 전체 롤백

        long count = productRepository.count();     // DB에 저장된 전체 상품 개수 확인
        assertEquals(1, count);             // dtoList 전체 롤백되어 현재 첫 dto 등록 1건만 존재(count = 1)
    }
}