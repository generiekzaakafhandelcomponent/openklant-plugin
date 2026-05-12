import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {
  FunctionConfigurationComponent,
  FunctionConfigurationData,
  PluginTranslatePipeModule
} from "@valtimo/plugin";
import {BehaviorSubject, combineLatest, Observable, ReplaySubject, Subscription, take} from "rxjs";
import {SetDefaultDigitaalAdresConfig} from "../../models/set-default-digitaal-adres-config";
import {AsyncPipe, NgIf} from "@angular/common";
import {FormModule, FormOutput, InputModule} from "@valtimo/components";

@Component({
  selector: 'set-default-digitaal-adres',
  imports: [
    AsyncPipe,
    FormModule,
    InputModule,
    NgIf,
    PluginTranslatePipeModule
  ],
  standalone: true,
  templateUrl: './set-default-digitaal-adres.component.html'
})
export class SetDefaultDigitaalAdresComponent
  implements FunctionConfigurationComponent, OnInit, OnDestroy {
  @Input() save$: Observable<void>;
  @Input() disabled$: Observable<boolean>;
  @Input() pluginId: string;
  @Input() prefillConfiguration$?: Observable<SetDefaultDigitaalAdresConfig>;
  @Output() valid: EventEmitter<boolean>;
  @Output() configuration: EventEmitter<FunctionConfigurationData> =
    new EventEmitter<FunctionConfigurationData>();

  private saveSubscription!: Subscription;

  private readonly config$ =
    new BehaviorSubject<SetDefaultDigitaalAdresConfig | null>(null);
  private readonly valid$ = new BehaviorSubject<boolean>(false);

  ngOnInit(): void {
    this.openSaveSubscription()
  }

  ngOnDestroy(): void {
    this.saveSubscription?.unsubscribe();
  }

  formValueChange(formOutput: FormOutput): void {
    this.config$.next(formOutput as SetDefaultDigitaalAdresConfig);
    this.handleValid(formOutput as SetDefaultDigitaalAdresConfig);
  }

  private handleValid(formOutput: SetDefaultDigitaalAdresConfig): void {
    const valid = !!formOutput.resultPvName &&
      !!formOutput.partijUuid &&
      !!formOutput.adres &&
      !!formOutput.soortDigitaalAdres &&
      !!formOutput.verificatieDatum;

    this.valid$.next(valid);
    this.valid.emit(valid);
  }

  private openSaveSubscription(): void {
    this.saveSubscription = this.save$?.subscribe((save) => {
      combineLatest([this.config$, this.valid$])
        .pipe(take(1))
        .subscribe(([config, valid]) => {
          if (valid && config) {
            this.configuration.emit(config);
          }
        });
    });
  }
}
