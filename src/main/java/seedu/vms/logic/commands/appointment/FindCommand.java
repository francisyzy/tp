package seedu.vms.logic.commands.appointment;

import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import seedu.vms.commons.core.Messages;
import seedu.vms.commons.core.index.Index;
import seedu.vms.commons.util.CollectionUtil;
import seedu.vms.logic.CommandMessage;
import seedu.vms.logic.commands.Command;
import seedu.vms.model.GroupName;
import seedu.vms.model.Model;
import seedu.vms.model.appointment.Appointment;
import seedu.vms.model.appointment.predicates.EndTimePredicate;
import seedu.vms.model.appointment.predicates.IndexPredicate;
import seedu.vms.model.appointment.predicates.StartTimePredicate;
import seedu.vms.model.appointment.predicates.VaccineContainsKeywordsPredicate;

/**
 * Finds and lists all patients in patient manager whose name contains any of the argument keywords.
 * Keyword matching is case insensitive.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";
    public static final String COMMAND_GROUP = "patient";

    public static final String MESSAGE_USAGE = COMMAND_GROUP + " " + COMMAND_WORD
            + ": Finds all patients whose names contain any of "
            + "the specified keywords (case-insensitive) and displays them as a list with index numbers.\n"
            + "Parameters: KEYWORD [MORE_KEYWORDS]...\n"
            + "Example: " + COMMAND_GROUP + " " + COMMAND_WORD + " alice bob charlie";

    private final Optional<IndexPredicate> indexPredicate;
    private final Optional<StartTimePredicate> startTimePredicate;
    private final Optional<EndTimePredicate> endTimePredicate;
    private final Optional<VaccineContainsKeywordsPredicate> vaccinePredicate;

    /**
     * Existing FindCommand that was previously used to search using name only
     *
     * @param indexPredicate
     */
    public FindCommand(IndexPredicate indexPredicate) {
        this.indexPredicate = Optional.of(indexPredicate);
        this.startTimePredicate = Optional.empty();
        this.endTimePredicate = Optional.empty();
        this.vaccinePredicate = Optional.empty();
    }

    /**
     * New FindCommand that contains more patient information that is given by the user.
     * Accepts different descriptors when applicable
     *
     * @param findAppointmentDescriptor
     */
    public FindCommand(FindAppointmentDescriptor findAppointmentDescriptor) {
        if (findAppointmentDescriptor.getPatient().isPresent()) {
            this.indexPredicate = Optional.of(new IndexPredicate(findAppointmentDescriptor.getPatient().get()));
        } else {
            this.indexPredicate = Optional.empty();
        }

        if (findAppointmentDescriptor.getAppointmentTime().isPresent()) {
            this.startTimePredicate = Optional.of(new StartTimePredicate(findAppointmentDescriptor.getAppointmentTime().get()));
        } else {
            this.startTimePredicate = Optional.empty();
        }

        if (findAppointmentDescriptor.getAppointmentEndTime().isPresent()) {
            this.endTimePredicate = Optional.of(new EndTimePredicate(findAppointmentDescriptor.getAppointmentEndTime().get()));
        } else {
            this.endTimePredicate = Optional.empty();
        }

        if (findAppointmentDescriptor.getVaccination().isPresent()) {
            this.vaccinePredicate = Optional
                    .of(new VaccineContainsKeywordsPredicate(findAppointmentDescriptor.getVaccination().get()));
        } else {
            this.vaccinePredicate = Optional.empty();
        }
    }

    @Override
    public CommandMessage execute(Model model) {
        requireNonNull(model);
        List<Optional<? extends Predicate<Appointment>>> optionalFilters = List.of(indexPredicate, vaccinePredicate);
        List<Predicate<Appointment>> filters = optionalFilters.stream()
                .filter(Objects::nonNull)
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
        model.setAppointmentFilters(filters);
        return new CommandMessage(
                String.format(Messages.MESSAGE_PATIENTS_LISTED_OVERVIEW, model.getFilteredAppointmentMap().size()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof FindCommand // instanceof handles Optional.empty()s
                        && indexPredicate.equals(((FindCommand) other).indexPredicate) // state check
                        && vaccinePredicate.equals(((FindCommand) other).vaccinePredicate)); // state check
    }

    /**
     * Stores the details to find the appointment with. Each non-empty field value will replace the
     * corresponding field value of the appointment.
     */
    public static class FindAppointmentDescriptor {
        private Index patientId;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private GroupName vaccine;
        private boolean isCompleted;

        public FindAppointmentDescriptor() {}

        /**
         * Copy constructor.
         */
        public FindAppointmentDescriptor(FindAppointmentDescriptor toCopy) {
            this.patientId = toCopy.patientId;
            this.startTime = toCopy.startTime;
            this.endTime = toCopy.endTime;
            this.vaccine = toCopy.vaccine;
            this.isCompleted = toCopy.isCompleted;
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(patientId, startTime, endTime, vaccine, isCompleted);
        }

        public Optional<Index> getPatient() {
            return Optional.ofNullable(patientId);
        }

        public Optional<LocalDateTime> getAppointmentTime() {
            return Optional.ofNullable(startTime);
        }

        public Optional<LocalDateTime> getAppointmentEndTime() {
            return Optional.ofNullable(endTime);
        }

        public Optional<GroupName> getVaccination() {
            return Optional.ofNullable(vaccine);
        }

        public Optional<Boolean> getStatus() {
            return Optional.of(isCompleted);
        }

        @Override
        public boolean equals(Object other) {
            // short circuit if same object
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof FindAppointmentDescriptor)) {
                return false;
            }

            // state check
            FindAppointmentDescriptor e = (FindAppointmentDescriptor) other;

            return getPatient().equals(e.getPatient())
                    && getAppointmentTime().equals(e.getAppointmentTime())
                    && getAppointmentEndTime().equals(e.getAppointmentEndTime())
                    && getVaccination().equals(e.getVaccination())
                    && getStatus().equals(e.getStatus());
        }
    }
}
