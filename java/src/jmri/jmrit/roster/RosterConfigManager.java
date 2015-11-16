package jmri.jmrit.roster;

import java.beans.PropertyChangeEvent;
import java.util.Locale;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.annotation.Nonnull;
import jmri.implementation.FileLocationsPreferences;
import jmri.profile.Profile;
import jmri.profile.ProfileUtils;
import jmri.spi.AbstractPreferencesProvider;
import jmri.spi.InitializationException;
import jmri.spi.PreferencesProvider;
import jmri.util.FileUtil;
import jmri.util.FileUtilSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Load and store the Roster configuration.
 *
 * This only configures the Roster when initialized so that configuration
 * changes made by users do not affect the running instance of JMRI, but only
 * take effect after restarting JMRI.
 *
 * @author Randall Wood (C) 2015
 */
public class RosterConfigManager extends AbstractPreferencesProvider {

    private String directory = FileUtil.getAbsoluteFilename(FileUtil.PREFERENCES);
    private String defaultOwner = "";

    public static final String DIRECTORY = "directory";
    public static final String DEFAULT_OWNER = "defaultOwner";
    private static final Logger log = LoggerFactory.getLogger(RosterConfigManager.class);

    public RosterConfigManager() {
        FileUtilSupport.getDefault().addPropertyChangeListener((PropertyChangeEvent evt) -> {
            if (RosterConfigManager.this.getDirectory().equals(evt.getOldValue())) {
                RosterConfigManager.this.setDirectory((String) evt.getNewValue());
            }
        });
    }

    @Override
    public void initialize(Profile profile) throws InitializationException {
        if (!this.isInitialized(profile)) {
            Preferences preferences = ProfileUtils.getPreferences(profile, this.getClass(), true);
            this.setDefaultOwner(preferences.get(DEFAULT_OWNER, this.getDefaultOwner()));
            try {
                this.setDirectory(preferences.get(DIRECTORY, this.getDirectory()));
            } catch (IllegalArgumentException ex) {
                this.setIsInitialized(profile, true);
                throw new InitializationException(
                        Bundle.getMessage(Locale.ENGLISH, "IllegalRosterLocation", preferences.get(DIRECTORY, this.getDirectory())),
                        ex.getMessage(),
                        ex);
            }
            Roster.getDefault().setRosterLocation(this.getDirectory());
            this.setIsInitialized(profile, true);
        }
    }

    @Override
    public void savePreferences(Profile profile) {
        Preferences preferences = ProfileUtils.getPreferences(profile, this.getClass(), true);
        preferences.put(DIRECTORY, FileUtil.getPortableFilename(this.getDirectory()));
        preferences.put(DEFAULT_OWNER, this.getDefaultOwner());
        try {
            preferences.sync();
        } catch (BackingStoreException ex) {
            log.error("Unable to save preferences", ex);
        }
    }

    @Override
    public Set<Class<? extends PreferencesProvider>> getRequires() {
        Set<Class<? extends PreferencesProvider>> requires = super.getRequires();
        requires.add(FileLocationsPreferences.class);
        return requires;
    }

    /**
     * @return the defaultOwner
     */
    public @Nonnull
    String getDefaultOwner() {
        // defaultOwner should never be null, but check anyway to ensure its not
        if (defaultOwner == null) {
            defaultOwner = "";
        }
        return defaultOwner;
    }

    /**
     * @param defaultOwner the defaultOwner to set
     */
    public void setDefaultOwner(String defaultOwner) {
        if (defaultOwner == null) {
            defaultOwner = "";
        }
        String oldDefaultOwner = this.defaultOwner;
        this.defaultOwner = defaultOwner;
        propertyChangeSupport.firePropertyChange(DEFAULT_OWNER, oldDefaultOwner, defaultOwner);
    }

    /**
     * @return the directory
     */
    public String getDirectory() {
        return directory;
    }

    /**
     * @param directory the directory to set
     */
    public void setDirectory(String directory) {
        if (directory == null || directory.isEmpty()) {
            directory = FileUtil.PREFERENCES;
        }
        String oldDirectory = this.directory;
        if (directory != null && FileUtil.getAbsoluteFilename(directory) == null) {
            throw new IllegalArgumentException(Bundle.getMessage("IllegalRosterLocation", directory));
        }
        this.directory = FileUtil.getAbsoluteFilename(directory);
        propertyChangeSupport.firePropertyChange(DIRECTORY, oldDirectory, directory);
    }

}