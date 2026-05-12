import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {FunctionConfigurationComponent, FunctionConfigurationData, PluginTranslatePipeModule} from "@valtimo/plugin";
import {BehaviorSubject, combineLatest, Observable, ReplaySubject, Subscription, take} from "rxjs";
import {GetOrCreatePartijConfig} from "../../models/get-or-create-partij-config";
import {FormModule, FormOutput, InputModule} from "@valtimo/components";
import {AsyncPipe, NgIf} from "@angular/common";

@Component({
  selector: 'lib-get-or-create-partij',
  standalone: true,
  imports: [
    FormModule,
    InputModule,
    PluginTranslatePipeModule,
    NgIf,
    AsyncPipe
  ],
  templateUrl: './get-or-create-partij.component.html',
})
export class GetOrCreatePartijComponent
  implements FunctionConfigurationComponent, OnInit, OnDestroy {
  @Input() save$: Observable<void>;
  @Input() disabled$: Observable<boolean>;
  @Input() pluginId: string;
  @Input() prefillConfiguration$: Observable<GetOrCreatePartijConfig>;

  @Output() valid = new EventEmitter<boolean>();
  @Output() configuration = new EventEmitter<FunctionConfigurationData>();

  private readonly config$ =
    new BehaviorSubject<GetOrCreatePartijConfig | null>(null);
  private readonly valid$ = new BehaviorSubject<boolean>(false);
  private saveSubscription: Subscription;

  ngOnInit(): void {
    this.openSaveSubscription();
  }

  ngOnDestroy(): void {
    this.saveSubscription?.unsubscribe();
  }

  formValueChange(formOutput: FormOutput): void {
    this.config$.next(formOutput as GetOrCreatePartijConfig);
    this.handleValid(formOutput as GetOrCreatePartijConfig);
  }

  private handleValid(formOutput: GetOrCreatePartijConfig): void {
    const valid =
      !!formOutput.bsn &&
      !!formOutput.voorletters &&
      !!formOutput.voornaam &&
      !!formOutput.voorvoegselAchternaam &&
      !!formOutput.achternaam

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
