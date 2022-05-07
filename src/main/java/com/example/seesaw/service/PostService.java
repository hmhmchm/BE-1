package com.example.seesaw.service;

import com.example.seesaw.dto.PostRequestDto;
import com.example.seesaw.dto.PostResponseDto;
import com.example.seesaw.model.Image;
import com.example.seesaw.model.Post;
import com.example.seesaw.model.Tag;
import com.example.seesaw.model.User;
import com.example.seesaw.repository.ImageRepository;
import com.example.seesaw.repository.PostRepository;
import com.example.seesaw.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final ImageRepository imageRepository;
    private final TagRepository tagRepository;
    private final S3Service s3Service;

    //단어장 작성
    @Transactional
    public PostResponseDto createPost(PostRequestDto requestDto, List<MultipartFile> files, User user) {

        // 단어장 작성시 이미지파일 없이 등록시 기본 이미지 파일로 올리기!
        String name = null;
        for(MultipartFile file:files){
            name = file.getOriginalFilename();
            System.out.println("file이름은~~~:" + name);
        }
        List<String> imagePaths = new ArrayList<>();
        if (name.equals("")) {
            imagePaths.add("기본이미지 AWS에 저장해서 주소넣기!");
        } else {
            imagePaths.addAll(s3Service.upload(files));
        }
        // 단어
        String title = requestDto.getTitle();
        // 내용
        String contents = requestDto.getContents();
        // videoUrl
        String videoUrl = requestDto.getVideoUrl();
        // 태그
        List<String> tagName = requestDto.getTagNames();

        if (title == null) {
            throw new IllegalArgumentException("등록할 단어를 적어주세요.");
        } else if (contents == null) {
            throw new IllegalArgumentException("등록할 단어의 내용을 적어주세요.");
        }

        //post 저장
        Post post = new Post(title, contents, videoUrl, user);
        postRepository.save(post);

        //이미지 URL 저장하기
        List<String> images = new ArrayList<>();
        for(String imageUrl : imagePaths){
            Image image = new Image(imageUrl, post);
            imageRepository.save(image);
            images.add(image.getImageUrl());
        }

        // tag 저장하기.
        List<String> tags = new ArrayList<>();
        for(String tagNames : tagName){
            Tag tag = new Tag(tagNames, post);
            tagRepository.save(tag);
            tags.add(tag.getTagName());
        }

        //return 값 생성
        return new PostResponseDto(post, images, tags);
    }

    // 중복체크
    public boolean wordCheck(String title) {
        return postRepository.existsByTitle(title);
    }

    // 단어장 수정
    public PostResponseDto updatePost(Long postId, PostRequestDto requestDto, List<MultipartFile> files, User user) {

        List<String> tagName = requestDto.getTagNames();
        // 해당 단어장
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("해당 단어장이 없습니다.")
        );

        if (requestDto.getContents() == null) {
            throw new IllegalArgumentException("등록할 단어의 내용을 적어주세요.");
        }

        //post update
        post.update(requestDto, user);

        // 단어장 작성시 이미지파일 없이 등록시 기본 이미지 파일로 올리기!

        String name = null;
        for(MultipartFile file:files){
            name = file.getOriginalFilename();
            System.out.println("file이름은~~~:" + name);
        }
        List<String> imagePaths = new ArrayList<>();
        if (name.equals("")) {
            imagePaths.add("기본이미지 AWS에 저장해서 주소넣기!");
        } else {
//            imagePaths.addAll(s3Service.update(postId, files));
        }

        //이미지 URL 저장
        List<String> images = new ArrayList<>();
        for(String imageUrl : imagePaths){
            Image image = new Image(imageUrl, post);
            imageRepository.save(image);
            images.add(image.getImageUrl());
        }

        // tag 저장
        List<String> tags = new ArrayList<>();
        for(String tagNames : tagName){
            Tag tag = new Tag(tagNames, post);
            tagRepository.save(tag);
            tags.add(tag.getTagName());
        }
        return new PostResponseDto(post, images, tags);
    }

}
