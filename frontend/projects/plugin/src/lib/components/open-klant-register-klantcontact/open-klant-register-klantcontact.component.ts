import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
  ViewChild,
} from "@angular/core";
import {CommonModule} from "@angular/common";
import {
  FormModule, FormOutput,
  InputModule,
  RadioModule,
  RadioValue,
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
  map,
  Observable, ReplaySubject,
  Subscription,
  take,
} from "rxjs";
import {RegisterKlantcontactConfig} from "../../models/register-klantcontact-config";
import {ToggleModule} from "carbon-components-angular";
import {Toggle} from "carbon-components-angular";

@Component({
  selector: "register-klantcontact",
  standalone: true,
  imports: [
    CommonModule,
    FormModule,
    InputModule,
    PluginTranslatePipeModule,
    AsyncPipe,
    RadioModule,
    ToggleModule,
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

  @ViewChild("hasBetrokkene") hasBetrokkene: Toggle;

  private saveSubscription: Subscription;
  private readonly config$ =
    new BehaviorSubject<RegisterKlantcontactConfig | null>(null);
  private readonly valid$ = new BehaviorSubject<boolean>(false);

  ngOnInit(): void {
    this.openSaveSubscription();
  }

  ngOnDestroy(): void {
    this.saveSubscription?.unsubscribe();
  }

  formValueChange(formOutput: FormOutput): void {
    this.config$.next(formOutput as RegisterKlantcontactConfig);
    this.handleValid(formOutput as RegisterKlantcontactConfig);
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
      combineLatest([this.config$, this.valid$])
        .pipe(take(1))
        .subscribe(([config, valid]) => {
          if (valid) {
            this.configuration.emit(config);
          }
        });
    });
  }
}
