package com.example.demo;

import com.example.demo.Domain.Common.Dtos.ProductDTO;
import com.example.demo.Domain.Common.Repository.ProductRepository;
import com.example.demo.Domain.Common.Service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

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
    @Test
    void registerBulk_중간실패_전체롤백() {
        fail("TODO: registerBulk 롤백 테스트 구현");
    }
}
