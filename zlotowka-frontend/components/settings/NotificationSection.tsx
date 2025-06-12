import GenericCard from "@/components/dashboard/cards/generic/GenericCard";
import { ToggleOption } from "@/components/settings/ToggleOption";
import { NotificationsSectionProps } from "@/interfaces/settings/Settings";

export const NotificationsSection: React.FC<NotificationsSectionProps> = ({
  notificationsByEmail,
  setNotificationsByEmail,
  notificationsByPhone,
  setNotificationsByPhone,
}) => (
  <GenericCard className="mt-10 max-w-3xl p-6" id="Powiadomienia">
    <h2 className="text-2xl md:text-3xl">Powiadomienia</h2>
    <div className="mt-4">
      <ToggleOption
        label="Na email"
        enabled={notificationsByEmail}
        onToggle={() => setNotificationsByEmail(!notificationsByEmail)}
      />
      <ToggleOption
        label="Na telefon"
        enabled={notificationsByPhone}
        onToggle={() => setNotificationsByPhone(!notificationsByPhone)}
      />
    </div>
  </GenericCard>
);
