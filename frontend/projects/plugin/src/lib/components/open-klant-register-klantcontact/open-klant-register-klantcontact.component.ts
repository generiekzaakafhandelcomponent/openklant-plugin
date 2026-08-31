import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
} from "@angular/core";
import {CommonModule} from "@angular/common";
import {
  FormModule, FormOutput,
  InputModule,
} from "@valtimo/components";
import {
  FunctionConfigurationComponent,
  FunctionConfigurationData,
  PluginTranslatePipeModule,
} from "@valtimo/plugin";
import {AsyncPipe} from "@angular/common";
import {
  BehaviorSubject,
  combineLatest,
  Observable,
  Subscription,
  take,
} from "rxjs";
import {RegisterKlantcontactConfig} from "../../models/register-klantcontact-config";

@Component({
  selector: "register-klantcontact",
  standalone: true,
  imports: [
    CommonModule,
    FormModule,
    InputModule,
    PluginTranslatePipeModule,
    AsyncPipe,
  ],
  templateUrl: "./open-klant-register-klantcontact.component.html",
})
export class RegisterKlantcontactComponent
  implements FunctionConfigurationComponent, OnInit, OnDestroy {
  @Input() save$: Observable<void>;
  @Input() disabled$: Observable<boolean>;
  @Input() pluginId: string;
  @Input()
  prefillConfiguration$?: Observable<RegisterKlantcontactConfig>;

  @Output() valid: EventEmitter<boolean> = new EventEmitter<boolean>();
  @Output() configuration: EventEmitter<FunctionConfigurationData> =
    new EventEmitter<FunctionConfigurationData>();

  protected hasBetrokkene = false;

  protected readonly formValue$ =
    new BehaviorSubject<RegisterKlantcontactConfig | null>(null);

  private saveSubscription: Subscription;
  private readonly valid$ = new BehaviorSubject<boolean>(false);

  ngOnInit(): void {
    this.openSaveSubscription();
  }

  ngOnDestroy(): void {
    this.saveSubscription?.unsubscribe();
  }

  formValueChange(formOutput: FormOutput): void {
    const formValue = formOutput as RegisterKlantcontactConfig;

    this.hasBetrokkene = !!formValue.hasBetrokkene;
    this.formValue$.next(formValue);
    this.handleValid(formValue);
  }

  private handleValid(formOutput: RegisterKlantcontactConfig): void {
    const valid =
      !!formOutput.kanaal &&
      !!formOutput.onderwerp &&
      !!formOutput.vertrouwelijk &&
      !!formOutput.taal &&
      !!formOutput.plaatsgevondenOp &&
      !!(!formOutput.hasBetrokkene || formOutput.partijUuid) &&
      !!(!formOutput.hasBetrokkene || formOutput.voorletters) &&
      !!(!formOutput.hasBetrokkene || formOutput.voornaam) &&
      !!(!formOutput.hasBetrokkene || formOutput.voorvoegselAchternaam) &&
      !!(!formOutput.hasBetrokkene || formOutput.achternaam);

    this.valid$.next(valid);
    this.valid.emit(valid);
  }

  private openSaveSubscription(): void {
    this.saveSubscription = this.save$?.subscribe(() => {
      combineLatest([this.formValue$, this.valid$])
        .pipe(take(1))
        .subscribe(([config, valid]) => {
          if (valid) {
            this.configuration.emit(config);
          }
        });
    });
  }
}
