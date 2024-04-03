package com.edohan.auraZen.service;

import com.edohan.auraZen.entity.Board;
import com.edohan.auraZen.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
public class BoardService {

    @Autowired
    public BoardRepository repository;

    public Page<Board> list(Pageable pageable) {

        return repository.findAll(pageable);
    }

    public Page<Board> searchList(String keyword, Pageable page) {

        return repository.findByTitleContaining(keyword, page);
    }

    public Board view(Integer id){
        Optional<Board> optionalBoard = repository.findById(id);
        return optionalBoard.orElse(null);
    }

    public void write(Board board, MultipartFile file) throws IOException {

        String path = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files";

        UUID uuid = UUID.randomUUID();

        String fileName = uuid + "_" + file.getOriginalFilename();

        File saveFile = new File(path, fileName);

        file.transferTo(saveFile);

        board.setFilename(fileName);

        board.setFilepath("/files/" + fileName);

        repository.save(board);
    }

    public void delete(Integer id) { repository.deleteById(id); }
}
