package com.edohan.auraZen.controller;

import com.edohan.auraZen.entity.Board;
import com.edohan.auraZen.service.BoardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/board")
public class BoardController {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private BoardService service;

    @GetMapping("/list")
    public String boardList(Model model,
                            @PageableDefault(page = 0, size = 20, sort = "id",direction = Sort.Direction.DESC) Pageable page,
                            @RequestParam(name = "keyword", required = false) String keyword){

        Page<Board> list = null;

        if(keyword == null){
            list = service.list(page);
        } else {
            list = service.searchList(keyword, page);
        }

        int nowPage = list.getPageable().getPageNumber() + 1;
        int startPage = Math.max(nowPage - 4 , 1);
        int endPage = Math.min(nowPage + 5, list.getTotalPages());

        model.addAttribute("list", list);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "/board/list";
    }
    @GetMapping("/view")
    public String boardView(Model model, @RequestParam("id") Integer id  ){
        model.addAttribute("board", service.view(id));
        return "/board/view";
    }

    @GetMapping("/modify/{id}")
    public String boardModifyForm(Model model, @PathVariable("id") Integer id) {
        model.addAttribute("board", service.view(id));
        return "/board/modify";
    }

    @PostMapping("/modify/{id}")
    public String boardModifyPro(Board board, @PathVariable("id") Integer id, Model model,@RequestParam("file") MultipartFile file) throws IOException {

        Board boardTemp = service.view(id);
        boardTemp.setTitle(board.getTitle());
        boardTemp.setContent(board.getContent());

        service.write(boardTemp, file);

        model.addAttribute("message", "글 수정이 완료되었습니다");
        model.addAttribute("url", "/board/list");

        return "/message";
    }
    @GetMapping("/write")
    public String boardWriteForm() {
        return "/board/write";
    }

    @PostMapping("/write")
    public String boardWritePro(Board board, Model model,@RequestParam("file") MultipartFile file) throws IOException {

        service.write(board, file);
    
        model.addAttribute("message", "글 작성이 완료되었습니다");
        model.addAttribute("url", "/board/list");

        return "/message";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") Integer id){
        service.delete(id);
        return "redirect:/board/list";
    }
}
