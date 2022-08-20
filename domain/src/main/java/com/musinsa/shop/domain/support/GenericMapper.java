package com.musinsa.shop.domain.support;

public interface GenericMapper<D, E> {
    D toDto(E e);
    E toEntity(D d);
}