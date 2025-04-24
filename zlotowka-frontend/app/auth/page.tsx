import { redirect } from "next/navigation";
import routes from "@/routes";

export default function AuthPage() {
  redirect(routes.login.pathname);
}
