package com.example.node.dao;

import com.example.node.entity.BinaryContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BinaryContentDAO extends JpaRepository<BinaryContent, Long> {
}
