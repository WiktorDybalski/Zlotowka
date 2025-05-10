import GenericCard from "@/components/dashboard/cards/generic/GenericCard";
import {ToggleOption} from "@/components/settings/ToggleOption";
import {PreferencesSectionProps} from "@/interfaces/settings/Settings";

export const PreferencesSection: React.FC<PreferencesSectionProps> = ({ darkMode, setDarkMode }) => (
    <GenericCard className="mt-10 max-w-3xl p-6" id="Preferencje">
      <h2 className="text-2xl md:text-3xl">Preferencje</h2>
      <div className="mt-4">
        <ToggleOption
            label="Ciemny motyw"
            enabled={darkMode}
            onToggle={() => setDarkMode(!darkMode)}
        />
      </div>
    </GenericCard>
);