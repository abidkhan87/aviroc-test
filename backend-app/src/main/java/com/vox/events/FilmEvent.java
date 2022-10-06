package com.vox.events;

import com.vox.api.data.Film;
import org.springframework.context.ApplicationEvent;

/**
 * @author abidkhan
 */
public class FilmEvent extends ApplicationEvent {


	private static final long serialVersionUID = 920736942431760100L;

	public FilmEvent(Film source) {
		super(source);
	}

	
}
