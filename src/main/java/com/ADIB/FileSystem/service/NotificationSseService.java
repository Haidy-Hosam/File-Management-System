package com.ADIB.FileSystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
public class NotificationSseService {

    private final Map<Long, List<SseEmitter>> emitterByUser = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long userId){
        SseEmitter emitter = new SseEmitter(0L);
        emitterByUser.computeIfAbsent(userId, id -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removeEmitter(userId, emitter));
        emitter.onTimeout(() -> removeEmitter(userId, emitter));
        emitter.onError(e -> removeEmitter(userId, emitter));

        return emitter;
    }

    public void push(Long userId, Object payload){
        List<SseEmitter> emitters = emitterByUser.get(userId);
        if(emitters == null) return;
        for(SseEmitter emitter : emitters){
            try{
                emitter.send(SseEmitter.event()
                        .name("file-notification")
                        .data(payload, MediaType.APPLICATION_JSON));
            }catch(IOException e){
                removeEmitter(userId, emitter);
            }
        }
    }

    private void removeEmitter(Long userId, SseEmitter emitter){
        List<SseEmitter> emitters = emitterByUser.get(userId);
        if ( emitters != null ) {
            emitters.remove(emitter);
        }
    }

}
