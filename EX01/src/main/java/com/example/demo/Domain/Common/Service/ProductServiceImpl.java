package com.example.demo.Domain.Common.Service;

import com.example.demo.Domain.Common.Dtos.ProductDTO;
import com.example.demo.Domain.Common.Entity.Product;
import com.example.demo.Domain.Common.Repository.ProductRepository;
import com.example.demo.Exception.MyBizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    // TODO: 상품 등록 
    //  - 메서드 위에 @Transactional 적용
    //  - productRepository.existsByName(이름) 으로 중복 검사 → 중복이면 MyBizException 발생
    //  - dto.setCreateAt(LocalDateTime.now()) 설정 후 productRepository.save(dto.toEntity())
    //  - 저장된 Entity 를 ProductDTO.from(...) 으로 변환하여 반환

    // 상품 등록
    @Override
    @Transactional // 해당 메서드 실행 중 발생하는 DB 작업을 하나의 트랜잭션으로 처리
    public ProductDTO register(ProductDTO dto) {

        if (productRepository.existsByName(dto.getName())) {                 // 상품명 중복 검사. 동일한 상품명이 DB에 존재하는지 확인
            throw new MyBizException("이미 존재하는 상품명입니다.");            // 상품명이 중복 존재하면 등록하지 않고 예외 발생시킴
        }
        dto.setCreateAt(LocalDateTime.now());                               // 등록 시간을 현재 시간으로 설정
        Product product = productRepository.save(dto.toEntity());           // DTO → Entity 로 변환해 db에 저장

        return ProductDTO.from(product);                                    // 저장된 Entity → DTO 형태로 변환해 반환
    }

    // TODO: 상품 수정
    //  - @Transactional 적용
    //  - productRepository.findById(id) (없으면 MyBizException)
    //  - existsByNameAndIdNot(이름, id) 로 자기 자신 제외 중복 검사
    //  - 조회한 Entity 의 필드를 dto 값으로 변경 → JPA 변경감지(dirty checking)로 update
    //  - 변경된 Entity 를 ProductDTO.from 으로 반환

    // 상품 수정
    @Override
    @Transactional
    public ProductDTO modify(Long id, ProductDTO dto) {

        Product product = productRepository
                .findById(id)                                       // id로 수정할 삼품 Entity 조회
                .orElseThrow(() -> new MyBizException("해당 상품을 찾을 수 없습니다.")); // id를 찾을 수 없으면 수정하지 않고 예외 발생시킴

        if (productRepository.existsByNameAndIdNot(dto.getName(), id)) { // 현재 id 제외 동일한 상품명이 DB에 존재하는지 상품명 중복 검사
            throw new MyBizException("이미 존재하는 상품명입니다.");        // 상품명이 중복 존재하면 등록하지 않고 예외 발생시킴
        }

        // Entity 필드 값을 조회해 현재 dto의 값으로 변경
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setCategory(dto.getCategory());

        return ProductDTO.from(product);    // 변경된 Entity → DTO 형태로 변환해 반환
    }

    // TODO: 상품 삭제
    //  - @Transactional 적용
    //  - productRepository.existsById(id) 확인 (없으면 MyBizException)
    //  - productRepository.deleteById(id)

    // 상품 삭제
    @Override
    @Transactional
    public void remove(Long id) {

        if (!productRepository.existsById(id)) {                      // 삭제할 상품 id 조회
            throw new MyBizException("해당 상품을 찾을 수 없습니다.");  // id를 찾을 수 없으면 삭제하지 않고 예외 발생시킴
        }

        productRepository.deleteById(id);                           // 해당 id 상품을 DB에서 삭제
    }

    // TODO: 단건 조회
    //  - @Transactional(readOnly = true) 적용
    //  - productRepository.findById(id) (없으면 MyBizException) → ProductDTO.from 으로 반환

    // 단건 조회
    @Override
    @Transactional(readOnly = true)     // 읽기 전용 트랜잭션 true, 성능 최적화
    public ProductDTO get(Long id) {

        Product product = productRepository
                .findById(id)                                   // id로 조회할 상품 Entity 조회
                .orElseThrow(() -> new MyBizException("해당 상품을 찾을 수 없습니다."));

        return ProductDTO.from(product);                        // 조회한 Entity → DTO 형태로 변환해 반환
    }

    // TODO: 목록 조회(페이징)
    //  - @Transactional(readOnly = true) 적용
    //  - productRepository.findAll(pageable) 결과를 .map(ProductDTO::from) 으로 변환하여 반환

    // 목록 조회(페이징)
    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> list(Pageable pageable) {

        return productRepository
                .findAll(pageable)          // 페이징 조건에 맞게 상품 목록 조회
                .map(ProductDTO::from);     // 조회한 Entity 목록 DTO 목록으로 변환
    }

    // TODO: 일괄 등록 (한 트랜잭션)
    //  - @Transactional 적용 (중간 실패 시 부분 저장이 남으면 안 됨)
    //  - list 를 순회하며 저장, 하나라도 existsByName 중복이면 MyBizException 발생 → 전체 롤백
    //  - 저장한 건수(int)를 반환

    // 일괄 등록(한 트랜잭션)
    @Override
    @Transactional
    public int registerBulk(List<ProductDTO> list) {

        for (ProductDTO dto : list) {                                 // 전달 받은 list를 순회
            if (productRepository.existsByName(dto.getName())) {     // 동일한 상품명이 DB에 존재하는지 상품명 중복 검사
                throw new MyBizException("이미 존재하는 상품명입니다.");// 상품명이 중복 존재하면 예외 발생시켜 트랜잭션 전체 롤백
            }
            dto.setCreateAt(LocalDateTime.now());                   // 등록 시간을 현재 시간으로 설정
            productRepository.save(dto.toEntity());                 // DTO → Entity 로 변환해 db에 저장
        }
        return list.size();     // 등록된 상품 건수 반환
    }

}
