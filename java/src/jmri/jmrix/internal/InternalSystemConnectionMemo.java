package jmri.jmrix.internal;

import java.util.ResourceBundle;
import jmri.InstanceManager;

/**
 * Lightweight class to denote that a system is active, and provide general
 * information.
 * <p>
 * Things this needed to do:
 * <ul>
 * <li>One of these must be automatically, transparently available - this is done by
 *      inheriting from jmri.InstanceManagerAutoDefault
 * <li>It must be possible to have more than one of these, so you can have
 *      multiple internal systems defined - each one keeps internal references
 *      to its objects
 * <li>It must make sure that its objects are available individually through the instance manager.
 * <li>But it also has to handle the ProxyManager special cases in the InstanceManager
 * </ul>
 *
 * @author Bob Jacobsen Copyright (C) 2010, 2016
 */
public class InternalSystemConnectionMemo extends jmri.jmrix.SystemConnectionMemo implements jmri.InstanceManagerAutoDefault {

    public InternalSystemConnectionMemo(boolean defaultInstanceType) {
        super("I", "Internal");
        InstanceManager.store(this, InternalSystemConnectionMemo.class); // also register as specific type
        register();
        this. defaultInstanceType = defaultInstanceType;
    }
    
    // invoked by i.e. InstanceManager via the InstanceManagerAutoDefault
    // mechanism, this creates a partial system
    public InternalSystemConnectionMemo() {
        this(true);
    }

    boolean configured = false;
    
    // if true, this is the default Internal instance, which 
    // only provides a subset of types
    boolean defaultInstanceType = false;
    
    /**
     * Configure the common managers for Internal connections. This puts the
     * common manager config in one place.
     * <p> Note: The Proxy system can cause some managers to be created early.
     * We don't call configureManagers in that case, as it causes an infinite loop.
     */
    public void configureManagers() {

        log.debug("Do configureManagers - doesn't pre-build anything");
        if (configured) log.warn("configureManagers called for a second time", new Exception("traceback"));
        configured = true;
    }

    private InternalSensorManager sensorManager;
    private InternalLightManager lightManager;
    private InternalReporterManager reporterManager;
    private InternalTurnoutManager turnoutManager;

    private jmri.managers.DefaultPowerManager powerManager;
    private InternalConsistManager consistManager;
    private jmri.jmrix.debugthrottle.DebugThrottleManager throttleManager;
    private jmri.progdebugger.DebugProgrammerManager programManager;

    public InternalSensorManager getSensorManager() {
        if (sensorManager == null) {
            log.debug("Create InternalSensorManager \"{}\" by request", getSystemPrefix());
            sensorManager = new InternalSensorManager(getSystemPrefix());
            // special due to ProxyManager support
            InstanceManager.setSensorManager(sensorManager);
        }
        return sensorManager;
    }

    public InternalLightManager getLightManager() {
        if (lightManager == null) {
            log.debug("Create InternalLightManager by request");
            lightManager = new InternalLightManager();
            // special due to ProxyManager support
            InstanceManager.setLightManager(lightManager);
        }
        return lightManager;
    }

    public InternalReporterManager getReporterManager() {
        if (reporterManager == null) {
            log.debug("Create InternalReporterManager by request");
            reporterManager = new InternalReporterManager();
            // special due to ProxyManager support
            InstanceManager.setReporterManager(reporterManager);
        }
        return reporterManager;
    }

    public InternalTurnoutManager getTurnoutManager() {
        if (turnoutManager == null) {
            log.debug("Create InternalTurnoutManager \"{}\" by request", getSystemPrefix());
            turnoutManager = new InternalTurnoutManager(getSystemPrefix());
            // special due to ProxyManager support
            InstanceManager.setTurnoutManager(turnoutManager);
        }
        return turnoutManager;
    }

    public jmri.managers.DefaultPowerManager getPowerManager() {
        if (powerManager == null) {
            log.debug("Create DefaultPowerManager by request");
            powerManager = new jmri.managers.DefaultPowerManager();
            jmri.InstanceManager.store(powerManager, jmri.PowerManager.class);
        }
        return powerManager;
    }

    @Override
    public InternalConsistManager getConsistManager() {
        if (defaultInstanceType) { return null; }
        if (consistManager == null) {
            log.debug("Create InternalConsistManager by request");
            consistManager = new InternalConsistManager();
            // special due to ProxyManager support
            InstanceManager.store(consistManager,jmri.ConsistManager.class);
        }
        return consistManager;
    }

    public jmri.jmrix.debugthrottle.DebugThrottleManager getThrottleManager() {
        if (defaultInstanceType) { return null; }
        if (throttleManager == null) {
            log.debug("Create DebugThrottleManager by request");
            // Install a debug throttle manager
            throttleManager = new jmri.jmrix.debugthrottle.DebugThrottleManager(this);
            jmri.InstanceManager.setThrottleManager(throttleManager);
        }
        return throttleManager;
    }

    public jmri.progdebugger.DebugProgrammerManager getProgrammerManager() {
        if (defaultInstanceType) { return null; }
        if (programManager == null) {
            log.debug("Create DebugProgrammerManager by request");
            // Install a debug programmer
            programManager = new jmri.progdebugger.DebugProgrammerManager(this);
            // Don't auto-enter, as that messes up selection in Single CV programmer
            //jmri.InstanceManager.setProgrammerManager(programManager);
        }
        return programManager;
    }

    @Override
    public boolean provides(Class<?> type) {
        if (getDisabled()) {
            return false;
        }

        if (!configured) configureManagers();

        if (type.equals(jmri.SensorManager.class)) {
            return true;
        }
        if (type.equals(jmri.LightManager.class)) {
            return true;
        }
        if (type.equals(jmri.ReporterManager.class)) {
            return true;
        }
        if (type.equals(jmri.TurnoutManager.class)) {
            return true;
        }


        if (!defaultInstanceType) {
            if (type.equals(jmri.PowerManager.class)) {
                return true;
            }
            
            if (type.equals(jmri.GlobalProgrammerManager.class)) {
                return getProgrammerManager().isGlobalProgrammerAvailable();
            }
            if (type.equals(jmri.AddressedProgrammerManager.class)) {
                return getProgrammerManager().isAddressedModePossible();
            }

            if (type.equals(jmri.ThrottleManager.class)) {
                return true;
            }
            if (type.equals(jmri.ConsistManager.class)) {
                return true;
            }
        }
                
        return super.provides(type);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Class<?> T) {
        if (getDisabled()) {
            return null;
        }

        if (!configured) configureManagers();

        if (T.equals(jmri.SensorManager.class)) {
            return (T) getSensorManager();
        }
        if (T.equals(jmri.LightManager.class)) {
            return (T) getLightManager();
        }
        if (T.equals(jmri.ReporterManager.class)) {
            return (T) getReporterManager();
        }
        if (T.equals(jmri.TurnoutManager.class)) {
            return (T) getTurnoutManager();
        }

        if (!defaultInstanceType) {
            if (T.equals(jmri.PowerManager.class)) {
                return (T) getPowerManager();
            }
            
            if (T.equals(jmri.GlobalProgrammerManager.class)) {
                return (T) getProgrammerManager();
            }
            if (T.equals(jmri.AddressedProgrammerManager.class)) {
                return (T) getProgrammerManager();
            }

            if (T.equals(jmri.ThrottleManager.class)) {
                return (T) getThrottleManager();
            }
            if (T.equals(jmri.ConsistManager.class)) {
                return (T) getConsistManager();
            }
        }

        return super.get(T);
    }

    @Override
    protected ResourceBundle getActionModelResourceBundle() {
        // No actions to add at start up
        return null;
    }

    @Override
    public void dispose() {
        if (sensorManager != null) {
            sensorManager.dispose();
            sensorManager = null;
        }
        if (turnoutManager != null) {
            turnoutManager.dispose();
            turnoutManager = null;
        }
        super.dispose();
    }

    private final static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(InternalSystemConnectionMemo.class);

}
