package org.zerock.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.domain.BoardVO;
import org.zerock.domain.Criteria;
import org.zerock.domain.PageDTO;
import org.zerock.service.BoardService;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;

@Controller
@Log4j
@RequestMapping("/board/*")
@AllArgsConstructor
public class BoardController {

	private BoardService service;
	
//	@GetMapping("/list")
//	public void list(Model model) {
//		log.info("list");
//		model.addAttribute("list", service.getList());
//	}
	@GetMapping("/list")
	public void list(@ModelAttribute("cri") Criteria cri, Model model) {
		log.info("list : " + cri);
		model.addAttribute("list", service.getList(cri));
		int total = service.getTotal(cri);
		model.addAttribute("pageMaker", new PageDTO(cri, total));
	}
	
	@GetMapping("/register")
	public void register(@ModelAttribute("cri") Criteria cri) {
		
	}
	@PostMapping("/register")
	public String register(BoardVO board, RedirectAttributes rttr) {
		log.info("register : " + board);
		service.register(board);
		rttr.addFlashAttribute("result", board.getBno());
		rttr.addFlashAttribute("message", board.getBno() + "번 글이 등록되었습니다.");
//		글 작성이 성공했다는 modal창을 글 목록 화면에서 띄우기 위해 
//		일회성 attribute인 redirectAttributes를 사용함 
		return "redirect:/board/list";
	}
	
	@GetMapping({"/get", "/modify"})  
	public void get(@RequestParam("bno") Long bno, @ModelAttribute("cri") Criteria cri, Model model) {
		log.info("/get");
		log.info(cri);
		model.addAttribute("board", service.get(bno));
//		model.addAttribute("cri", cri);
//		@ModelAttribute어노테이션으로 model에 변수이름으로 넣어둘 수 있음
	}
	//GET 방식의 modify URL이랑 get URL의 하는 일이 동일하므로
	//URL경로를 String배열로 주어서 코드 단축
//	@GetMapping("/modify")
//	public void modify(Long bno, Model model) {
//		BoardVO board = service.get(bno);
//		model.addAttribute("board", board);
//	}
	@PostMapping("/modify")
	public String modify(BoardVO board, Criteria cri, RedirectAttributes rttr) {
		log.info("modify : " + board);
		
		if(service.modify(board)) {
			rttr.addFlashAttribute("result", "success");
			rttr.addFlashAttribute("message", board.getBno() + "번 글이 수정되었습니다.");
			//rttr.addAttribute("a", "a"); -> 그냥 addAttribute를 하면
			//redirect를 해도 쿼리스트링으로 파라미터를 붙일 수 있음(일회성X)
		}
		log.info(cri);
		rttr.addAttribute("pageNum", cri.getPageNum());
		rttr.addAttribute("amount", cri.getAmount());
		rttr.addAttribute("type", cri.getType());
		rttr.addAttribute("keyword", cri.getKeyword());
		return "redirect:/board/list";
	}
	
	@PostMapping("/remove")
	public String remove(@RequestParam("bno") Long bno, Criteria cri, RedirectAttributes rttr) {
		log.info("remove : " + bno);
		if(service.remove(bno)) {
			rttr.addFlashAttribute("result", "success");
			rttr.addFlashAttribute("message", bno + "번 글이 삭제되었습니다.");
		}
		log.info(cri);
		rttr.addAttribute("pageNum", cri.getPageNum());
		rttr.addAttribute("amount", cri.getAmount());
		rttr.addAttribute("type", cri.getType());
		rttr.addAttribute("keyword", cri.getKeyword());
		return "redirect:/board/list";
	}
}
