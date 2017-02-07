package com.maths22.ftc.controllers;

import com.maths22.ftc.entities.Division;
import com.maths22.ftc.entities.Event;
import com.maths22.ftc.entities.Match;
import com.maths22.ftc.entities.TeamEventAssignment;
import com.maths22.ftc.enums.MatchType;
import com.maths22.ftc.repositories.DivisionRepository;
import com.maths22.ftc.repositories.EventRepository;
import com.maths22.ftc.repositories.TeamEventAssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Jacob on 2/4/2017.
 */
@Controller
public class TeamListController {
    @Autowired
    private  EventRepository eventRepository;

    @Autowired
    private DivisionRepository divisionRepository;

    @Autowired
    private TeamEventAssignmentRepository teamEventAssignmentRepository;

    @RequestMapping("/{eventKey}/teamList")
    public String matchListByEvent(@PathVariable String eventKey, Model model) {
        Optional<Event> event = eventRepository.findByKey(eventKey);

        if(!event.isPresent()) {
            throw new TeamListController.ResourceNotFoundException("Invalid eventKey " + eventKey);
        }

        Collection<Division> divisions = divisionRepository.getByEventId(event.get().getId());

        Map<Division, List<TeamEventAssignment>> teams = new HashMap<>();
        for(Division div : divisions) {
            List<TeamEventAssignment> teamList = teamEventAssignmentRepository.findByDivisionId(div.getId());
            teamList.sort(Comparator.comparing(ta -> ta.getTeam().getNumber()));
            teams.put(div, teamList);
        }
        model.addAttribute("divisions", divisions);
        model.addAttribute("teams", teams);

        return "event/teamList";
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String msg) {
            super(msg);
        }
    }

}
