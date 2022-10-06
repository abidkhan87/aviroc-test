package com.vox.events;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.FluxSink;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

/**
 * @author abidkhan
 */
@Component
@Log4j2
public class FilmEventPublisher implements ApplicationListener<FilmEvent>, 
		Consumer<FluxSink<FilmEvent>> { 

	private final Executor executor;
	private final BlockingQueue<FilmEvent> queue;

	FilmEventPublisher(Executor executor) {
		this.executor = executor;
		this.queue = new LinkedBlockingQueue<>();
	}

	/**
	 * Publish the event on the blocking queue
	 */
	@Override
	public void onApplicationEvent(FilmEvent event) {
		log.info("Event fired: {}", event);
		this.queue.offer(event);
	}

	/**
	 * provide the sink to consume the event
	 */
	@Override
	public void accept(FluxSink<FilmEvent> sink) {
		
 		this.executor.execute(() -> {
			while (true)
				try {
					FilmEvent event = queue.take(); // <5>
					sink.next(event); // <6>
				} catch (InterruptedException e) {
					ReflectionUtils.rethrowRuntimeException(e);
				}
		});
	}
}
