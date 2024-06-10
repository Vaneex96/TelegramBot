package com.example.node.dao;

import com.example.node.entity.AppDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppDocumentDAO extends JpaRepository<AppDocument, Long> {
}
