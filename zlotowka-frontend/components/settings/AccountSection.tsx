import GenericCard from "@/components/dashboard/cards/generic/GenericCard";
import {AccountField} from "@/components/settings/AccountField";
import {AccountSectionProps} from "@/interfaces/settings/Settings";

export const AccountSection: React.FC<AccountSectionProps> = ({ accountOptions }) => (
    <GenericCard className="mt-10 max-w-3xl p-6" id="Konto">
      <h2 className="text-2xl xl:text-3xl">Informacje ogólne</h2>
      <div className="mt-4">
        {accountOptions.map((option, index) => (
            <AccountField
                key={index}
                text={option.text}
                value={option.value}
                placeholder={option.placeholder}
                avatar={option.avatar}
                onClick={option.onClick}
            />
        ))}
      </div>
    </GenericCard>
);