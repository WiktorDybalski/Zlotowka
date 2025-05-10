import DarkButton from "@/components/DarkButton";
import {ToggleOptionProps} from "@/interfaces/settings/Settings";

export const ToggleOption: React.FC<ToggleOptionProps> = ({ label, enabled, onToggle }) => (
    <div className="flex justify-between items-center border-t-2 border-dashed border-neutral-200 py-4">
      <h4 className="text-md md:text-lg">{label}</h4>
      <div className="w-22 sm:w-28 h-10">
        <DarkButton
            className={`${
                enabled
                    ? "bg-accent text-background"
                    : "bg-neutral-400 text-accent"
            }`}
            text={enabled ? "Włączony" : "Wyłączony"}
            onClick={onToggle}
        />
      </div>
    </div>
);