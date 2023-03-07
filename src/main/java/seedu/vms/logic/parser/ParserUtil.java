package seedu.vms.logic.parser;

import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import seedu.vms.commons.core.index.Index;
import seedu.vms.commons.util.StringUtil;
import seedu.vms.logic.parser.exceptions.ParseException;
import seedu.vms.model.patient.Allergy;
import seedu.vms.model.patient.BloodType;
import seedu.vms.model.patient.Dob;
import seedu.vms.model.patient.Name;
import seedu.vms.model.patient.Phone;
import seedu.vms.model.patient.Vaccine;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX = "Index is not a non-zero unsigned integer.";
    public static final String MESSAGE_INVALID_DATE = "Date is of an invalid format";

    private static final String DEFAULT_DATE_REGEX = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}";
    private static final String FULL_DATE_REGEX = "\\d{4}-\\d{1,2}-\\d{1,2} \\d{4}";
    private static final String DATE_ONLY_REGEX = "\\d{4}-\\d{1,2}-\\d{1,2}";

    private static final String FULL_DATE_PATTERN = "yyyy-M-d HHmm";

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     *
     * @throws ParseException if the specified index is invalid (not non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!Name.isValidName(trimmedName)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(trimmedName);
    }

    /**
     * Parses a {@code String phone} into a {@code Phone}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code phone} is invalid.
     */
    public static Phone parsePhone(String phone) throws ParseException {
        requireNonNull(phone);
        String trimmedPhone = phone.trim();
        if (!Phone.isValidPhone(trimmedPhone)) {
            throw new ParseException(Phone.MESSAGE_CONSTRAINTS);
        }
        return new Phone(trimmedPhone);
    }

    /**
     * Parses a {@code String dob} into an {@code Dob}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code dob} is invalid.
     */
    public static Dob parseDob(String dob) throws ParseException {
        requireNonNull(dob);
        String trimmedDob = dob.trim();
        if (!Dob.isValidDob(trimmedDob)) {
            throw new ParseException(Dob.MESSAGE_CONSTRAINTS);
        }
        return new Dob(trimmedDob);
    }

    /**
     * Parses a {@code String bloodType} into an {@code BloodType}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code bloodType} is invalid.
     */
    public static BloodType parseBloodType(String bloodType) throws ParseException {
        requireNonNull(bloodType);
        String trimmedBloodType = bloodType.trim();
        if (!BloodType.isValidBloodType(trimmedBloodType)) {
            throw new ParseException(BloodType.MESSAGE_CONSTRAINTS);
        }
        return new BloodType(trimmedBloodType);
    }

    /**
     * Parses a {@code String allergy} into a {@code Allergy}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code allergy} is invalid.
     */
    public static Allergy parseAllergy(String allergy) throws ParseException {
        requireNonNull(allergy);
        String trimmedAllergy = allergy.trim();
        if (!Allergy.isValidAllergyName(trimmedAllergy)) {
            throw new ParseException(Allergy.MESSAGE_CONSTRAINTS);
        }
        return new Allergy(trimmedAllergy);
    }

    /**
     * Parses {@code Collection<String> allergies} into a {@code Set<Allergy>}.
     */
    public static Set<Allergy> parseAllergies(Collection<String> allergies) throws ParseException {
        requireNonNull(allergies);
        final Set<Allergy> allergySet = new HashSet<>();
        for (String allergyName : allergies) {
            allergySet.add(parseAllergy(allergyName));
        }
        return allergySet;
    }

    /**
     * Parses a {@code String vaccine} into a {@code Vaccine}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code vaccine} is invalid.
     */
    public static Vaccine parseVaccine(String vaccine) throws ParseException {
        requireNonNull(vaccine);
        String trimmedVaccine = vaccine.trim();
        if (!Vaccine.isValidVaccineName(trimmedVaccine)) {
            throw new ParseException(Vaccine.MESSAGE_CONSTRAINTS);
        }
        return new Vaccine(trimmedVaccine);
    }

    /**
     * Parses {@code Collection<String> vaccines} into a {@code Set<Vaccine>}.
     */
    public static Set<Vaccine> parseVaccines(Collection<String> vaccines) throws ParseException {
        requireNonNull(vaccines);
        final Set<Vaccine> vaccineSet = new HashSet<>();
        for (String vaccineName : vaccines) {
            vaccineSet.add(parseVaccine(vaccineName));
        }
        return vaccineSet;
    }

    /**
     * Parses a String date to a {@code LocalDateTime}.
     * <p>
     * The following formats are supported:
     * <ul>
     * <li>{@code yyyy-MM-dd'T'hh:mm}
     * <li>{@code yyyy-M-d hhmm}
     * </ul>
     *
     * @param dateString - the String date to parse.
     * @return the parsed {@code LocalDateTime}.
     * @throws ParseException if the given String cannot be parsed.
     */
    public static LocalDateTime parseDate(String dateString) throws ParseException {
        try {
            return parseCustomDate(dateString);
        } catch (DateTimeParseException dateParseEx) {
            throw new ParseException(dateParseEx.getMessage());
        }
    }

    private static LocalDateTime parseCustomDate(String dateString) throws ParseException {
        if (dateString.matches(DEFAULT_DATE_REGEX)) {
            // yyyy-MM-dd'T'hh:mm format
            return LocalDateTime.parse(dateString);
        } else if (dateString.matches(FULL_DATE_REGEX)) {
            // yyyy-MM-dd hhmm format
            return LocalDateTime.parse(
                    dateString,
                    DateTimeFormatter.ofPattern(FULL_DATE_PATTERN));
        } else if (dateString.matches(DATE_ONLY_REGEX)) {
            return LocalDateTime.parse(
                    dateString + " 0000",
                    DateTimeFormatter.ofPattern(FULL_DATE_PATTERN));
        }
        throw new ParseException(MESSAGE_INVALID_DATE);
    }
}
